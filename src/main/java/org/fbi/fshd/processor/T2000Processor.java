package org.fbi.fshd.processor;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.fbi.fshd.domain.cbs.T2000Request.CbsTia2000;
import org.fbi.fshd.domain.cbs.T2000Request.CbsTia2000Item;
import org.fbi.fshd.domain.cbs.T2000Response.CbsToa2000;
import org.fbi.fshd.domain.cbs.T2000Response.CbsToa2000Item;
import org.fbi.fshd.domain.tps.T2000Request.TpsTia2000;
import org.fbi.fshd.domain.tps.T2000Request.TpsTia2000Item;
import org.fbi.fshd.domain.tps.T2000Response.TpsToa2000;
import org.fbi.fshd.domain.tps.T2000Response.TpsToa2000Item;
import org.fbi.fshd.enums.BillStatus;
import org.fbi.fshd.enums.TxnRtnCode;
import org.fbi.fshd.helper.FbiBeanUtils;
import org.fbi.fshd.helper.MybatisFactory;
import org.fbi.fshd.helper.ProjectConfigManager;
import org.fbi.fshd.repository.dao.FsHdPaymentInfoMapper;
import org.fbi.fshd.repository.dao.FsHdPaymentItemMapper;
import org.fbi.fshd.repository.model.FsHdPaymentInfo;
import org.fbi.fshd.repository.model.FsHdPaymentInfoExample;
import org.fbi.fshd.repository.model.FsHdPaymentItem;
import org.fbi.fshd.repository.model.FsHdPaymentItemExample;
import org.fbi.linking.codec.dataformat.FixedLengthTextDataFormat;
import org.fbi.linking.codec.dataformat.SeperatedTextDataFormat;
import org.fbi.linking.processor.ProcessorException;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorRequest;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhanrui on 14-1-20.
 * 手工票缴款提交交易
 */
public class T2000Processor extends AbstractTxnProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        CbsTia2000 cbsTia;
        try {
            cbsTia = unmarshalCbsRequestMsg(request.getRequestBody());
        } catch (Exception e) {
            logger.error("特色业务平台请求报文解析错误.", e);
            marshalAbnormalCbsResponse(TxnRtnCode.CBSMSG_UNMARSHAL_FAILED, null, response);
            return;
        }

        //检查本地数据库信息
        FsHdPaymentInfo paymentInfo_db = selectNotCanceledPaymentInfoFromDB(cbsTia.getBillId());
        if (paymentInfo_db != null) {
            String billStatus = paymentInfo_db.getLnkBillStatus();
            if (billStatus.equals(BillStatus.PAYOFF.getCode())) { //已缴款
                marshalAbnormalCbsResponse(TxnRtnCode.TXN_PAY_REPEATED, null, response);
                logger.info("===此笔缴款单已缴款.");
                return;
            }else if (!billStatus.equals(BillStatus.INIT.getCode())) {  //非初始状态
                marshalAbnormalCbsResponse(TxnRtnCode.TXN_EXECUTE_FAILED, "此笔缴款单状态错误", response);
                logger.info("===此笔缴款单状态错误.");
                return;
            }
        }

        //第三方通讯处理
        TpsTia2000 tpsTia = new TpsTia2000();
        TpsToa2000 tpsToa;

        try {
            FbiBeanUtils.copyProperties(cbsTia, tpsTia);
            tpsTia.setTxnHdlCode("1");   //处理码 内容：1—表示请求验证
            tpsTia.setFisActno(ProjectConfigManager.getInstance().getProperty("tps.fis.actno"));
            tpsTia.setFisBatchSn("000001");   //批次号码信息
            tpsTia.setBranchId(request.getHeader("branchId"));
            tpsTia.setTlrId(request.getHeader("tellerId"));

            List<TpsTia2000Item>  tpsTiaItems  = new ArrayList<>();
            for (CbsTia2000Item cbsTiaItem : cbsTia.getItems()) {
                TpsTia2000Item tpsTiaItem = new TpsTia2000Item();
                FbiBeanUtils.copyProperties(cbsTiaItem, tpsTiaItem);
                tpsTiaItems.add(tpsTiaItem);
            }
            tpsTia.setItems(tpsTiaItems);

            byte[] recvTpsBuf = processThirdPartyServer(marshalTpsRequestMsg(tpsTia), "2000");
            tpsToa = unmarshalTpsResponseMsg(recvTpsBuf);
        } catch (SocketTimeoutException e) {
            logger.error("与第三方服务器通讯处理超时.", e);
            response.setHeader("rtnCode", TxnRtnCode.MSG_RECV_TIMEOUT.getCode());
            return;
        } catch (Exception e) {
            logger.error("与第三方服务器通讯处理异常.", e);
            response.setHeader("rtnCode", TxnRtnCode.MSG_COMM_ERROR.getCode());
            return;
        }

        //特色平台响应
        if ("0".equals(tpsToa.getRtnCode())) { //交易成功
            try {
                processTxn(cbsTia, tpsToa, request);
                String cbsRespMsg = marshalCbsResponseMsg(tpsToa);
                response.setHeader("rtnCode", TxnRtnCode.TXN_EXECUTE_SECCESS.getCode());
                response.setResponseBody(cbsRespMsg.getBytes(response.getCharacterEncoding()));
            } catch (Exception e) {
                marshalAbnormalCbsResponse(TxnRtnCode.TXN_EXECUTE_FAILED, e.getMessage(), response);
                logger.error("业务处理失败.", e);
            }
        } else {  //处理TPS返回错误码
            String errmsg = getTpsRtnErrorMsg(tpsToa.getRtnCode());
            marshalAbnormalCbsResponse(TxnRtnCode.TXN_EXECUTE_FAILED, errmsg, response);
        }
    }

    //解包生成CBS请求报文BEAN
    private CbsTia2000 unmarshalCbsRequestMsg(byte[] body) throws Exception {
        CbsTia2000 tia = new CbsTia2000();
        SeperatedTextDataFormat dataFormat = new SeperatedTextDataFormat(tia.getClass().getPackage().getName());
        tia = (CbsTia2000) dataFormat.fromMessage(new String(body, "GBK"), "CbsTia2000");
        return tia;
    }

    //根据本地数据库中的已保存信息生成CBS响应报文
    private String generateCbsRespMsgByLocalDbInfo(FsHdPaymentInfo paymentInfo, List<FsHdPaymentItem> paymentItems) {
        CbsToa2000 cbsToa = new CbsToa2000();
        FbiBeanUtils.copyProperties(paymentInfo, cbsToa);

        List<CbsToa2000Item> cbsToaItems = new ArrayList<>();
        for (FsHdPaymentItem paymentItem : paymentItems) {
            CbsToa2000Item cbsToaItem = new CbsToa2000Item();
            FbiBeanUtils.copyProperties(paymentItem, cbsToaItem);
            cbsToaItems.add(cbsToaItem);
        }
        cbsToa.setItems(cbsToaItems);
        cbsToa.setItemNum("" + cbsToaItems.size());

        String cbsRespMsg = "";
        Map<String, Object> modelObjectsMap = new HashMap<String, Object>();
        modelObjectsMap.put(cbsToa.getClass().getName(), cbsToa);
        SeperatedTextDataFormat cbsDataFormat = new SeperatedTextDataFormat(cbsToa.getClass().getPackage().getName());
        try {
            cbsRespMsg = (String) cbsDataFormat.toMessage(modelObjectsMap);
        } catch (Exception e) {
            throw new RuntimeException("特色平台报文转换失败.", e);
        }
        return cbsRespMsg;
    }


    //组第三方服务器请求报文
    private byte[] marshalTpsRequestMsg(TpsTia2000 tpsTia) {
        Map<String, Object> modelObjectsMap = new HashMap<String, Object>();
        modelObjectsMap.put(tpsTia.getClass().getName(), tpsTia);
        FixedLengthTextDataFormat dataFormat = new FixedLengthTextDataFormat(tpsTia.getClass().getPackage().getName());
        byte[] buf;
        try {
            String sendMsg = (String) dataFormat.toMessage(modelObjectsMap);
            buf = generateTpsRequestHeader(sendMsg).getBytes(TPS_ENCODING);
        } catch (Exception e) {
            throw new RuntimeException("第三方请求报文处理错误");
        }

        return buf;
    }

    //解包生成第三方响应报文BEAN
    private TpsToa2000 unmarshalTpsResponseMsg(byte[] response) throws Exception {
        TpsToa2000 toa = new TpsToa2000();
        FixedLengthTextDataFormat dataFormat = new FixedLengthTextDataFormat(toa.getClass().getPackage().getName());
        toa = (TpsToa2000) dataFormat.fromMessage(response, "TpsToa2000");

        return toa;
    }

    //根据第三方服务器响应报文生成特色平台响应报文
    private String marshalCbsResponseMsg(TpsToa2000 tpsToa) {
        CbsToa2000 cbsToa = new CbsToa2000();
        FbiBeanUtils.copyProperties(tpsToa, cbsToa);
        List<CbsToa2000Item> cbsToa2000Items = new ArrayList<>();
        for (TpsToa2000Item tpstoaItem : tpsToa.getItems()) {
            CbsToa2000Item cbsToaItem = new CbsToa2000Item();
            FbiBeanUtils.copyProperties(tpstoaItem, cbsToaItem);
            cbsToa2000Items.add(cbsToaItem);
        }
        cbsToa.setItems(cbsToa2000Items);

        String cbsRespMsg = "";
        Map<String, Object> modelObjectsMap = new HashMap<String, Object>();
        modelObjectsMap.put(cbsToa.getClass().getName(), cbsToa);
        SeperatedTextDataFormat cbsDataFormat = new SeperatedTextDataFormat(cbsToa.getClass().getPackage().getName());
        try {
            cbsRespMsg = (String) cbsDataFormat.toMessage(modelObjectsMap);
        } catch (Exception e) {
            throw new RuntimeException("特色平台报文转换失败.", e);
        }
        return cbsRespMsg;
    }

    //=======数据库处理=================================================
    //查找未撤销的缴款单记录
    private FsHdPaymentInfo selectNotCanceledPaymentInfoFromDB(String billId) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        FsHdPaymentInfoMapper mapper;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            mapper = session.getMapper(FsHdPaymentInfoMapper.class);
            FsHdPaymentInfoExample example = new FsHdPaymentInfoExample();
            example.createCriteria()
                    .andBillIdEqualTo(billId)
                    .andLnkBillStatusNotEqualTo(BillStatus.CANCELED.getCode());
            List<FsHdPaymentInfo> infos = mapper.selectByExample(example);
            if (infos.size() == 0) {
                return null;
            }
            if (infos.size() != 1) { //同一个缴款单号，未撤销的在表中只能存在一条记录
                throw new RuntimeException("记录状态错误.");
            }
            return infos.get(0);
        }
    }

    private List<FsHdPaymentItem> selectPaymentItemsFromDB(FsHdPaymentInfo paymentInfo) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            FsHdPaymentItemExample example = new FsHdPaymentItemExample();
            example.createCriteria().andMainPkidEqualTo(paymentInfo.getPkid());
            FsHdPaymentItemMapper infoMapper = session.getMapper(FsHdPaymentItemMapper.class);
            return infoMapper.selectByExample(example);
        }
    }


    private void processTxn(CbsTia2000 cbsTia, TpsToa2000 tpsToa, Stdp10ProcessorRequest request) {
        FsHdPaymentInfo paymentInfo = new FsHdPaymentInfo();
        FbiBeanUtils.copyProperties(cbsTia, paymentInfo);

        paymentInfo.setFisBizId(tpsToa.getFisBizId());
        paymentInfo.setInstName(tpsToa.getInstName());

        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            paymentInfo.setPkid(UUID.randomUUID().toString());
            paymentInfo.setOperInitBankid(request.getHeader("branchId"));
            paymentInfo.setOperInitTlrid(request.getHeader("tellerId"));
            paymentInfo.setOperInitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            paymentInfo.setOperInitTime(new SimpleDateFormat("HHmmss").format(new Date()));
            paymentInfo.setOperInitHostsn(request.getHeader("serialNo"));

            paymentInfo.setArchiveFlag("0");

            paymentInfo.setHostBookFlag("0");
            paymentInfo.setHostChkFlag("0");
            paymentInfo.setFbBookFlag("0");
            paymentInfo.setFbChkFlag("0");

            paymentInfo.setAreaCode("HuangDao-FeiShui");
            paymentInfo.setHostAckFlag("0");
            paymentInfo.setLnkBillStatus("0"); //初始化
            paymentInfo.setManualFlag("1"); //手工票

            FsHdPaymentInfoMapper infoMapper = session.getMapper(FsHdPaymentInfoMapper.class);
            infoMapper.insert(paymentInfo);

            FsHdPaymentItemMapper itemMapper = session.getMapper(FsHdPaymentItemMapper.class);
            int i = 0;
            for (TpsToa2000Item toaItem : tpsToa.getItems()) {
                i++;
                FsHdPaymentItem paymentItem = new FsHdPaymentItem();
                FbiBeanUtils.copyProperties(toaItem, paymentItem);
                paymentItem.setMainPkid(paymentInfo.getPkid());
                itemMapper.insert(paymentItem);
            }
            session.commit();
        } catch (Exception e) {
            session.rollback();
            throw new RuntimeException("业务逻辑处理失败。", e);
        } finally {
            session.close();
        }
    }
}

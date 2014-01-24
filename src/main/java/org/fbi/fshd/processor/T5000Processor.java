package org.fbi.fshd.processor;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.fbi.fshd.domain.cbs.T5000Request.CbsTia5000;
import org.fbi.fshd.domain.cbs.T5000Response.CbsToa5000;
import org.fbi.fshd.domain.cbs.T5000Response.CbsToa5000Item;
import org.fbi.fshd.domain.tps.T5000Request.TpsTia5000;
import org.fbi.fshd.domain.tps.T5000Request.TpsTia5000Item;
import org.fbi.fshd.domain.tps.T5000Response.TpsToa5000;
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
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanrui on 14-1-24.
 * 对账交易
 */
public class T5000Processor extends AbstractTxnProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        CbsTia5000 cbsTia;
        try {
            cbsTia = unmarshalCbsRequestMsg(request.getRequestBody());
        } catch (Exception e) {
            logger.error("特色业务平台请求报文解析错误.", e);
            marshalAbnormalCbsResponse(TxnRtnCode.CBSMSG_UNMARSHAL_FAILED, null, response);
            return;
        }

        //检查本地数据库信息
        List<FsHdPaymentInfo> paymentInfos = selectNotCanceledPaymentInfosFromDB(cbsTia.getStratDate(), cbsTia.getEndDate());
        List<CbsToa5000Item> cbsToaItems = new ArrayList<>();
        boolean result = true;

        int totalCnt = 0;
        BigDecimal totalAmt = new BigDecimal("0.00");
        int succCnt = 0;
        BigDecimal succAmt = new BigDecimal("0.00");
        int failCnt = 0;
        BigDecimal failAmt = new BigDecimal("0.00");

        for (FsHdPaymentInfo paymentInfo : paymentInfos) {
            totalCnt++;
            totalAmt = totalAmt.add(paymentInfo.getPayAmt());
            List<FsHdPaymentItem> paymentItems = selectPaymentItemsFromDB(paymentInfo);
            //第三方通讯处理
            TpsTia5000 tpsTia = new TpsTia5000();
            TpsToa5000 tpsToa;

            try {
                FbiBeanUtils.copyProperties(paymentInfo, tpsTia);
                tpsTia.setFisCode(ProjectConfigManager.getInstance().getProperty("tps.fis.fiscode"));
                tpsTia.setTxnHdlCode("A");   //处理码
                tpsTia.setFisActno(ProjectConfigManager.getInstance().getProperty("tps.fis.actno"));
                tpsTia.setFisBatchSn("000001");   //批次号码信息

                List<TpsTia5000Item> tpsTiaItems = new ArrayList<>();
                for (FsHdPaymentItem paymentItem : paymentItems) {
                    TpsTia5000Item tpsTiaItem = new TpsTia5000Item();
                    FbiBeanUtils.copyProperties(paymentItem, tpsTiaItem);
                    tpsTiaItems.add(tpsTiaItem);
                }
                tpsTia.setItems(tpsTiaItems);
                tpsTia.setItemNum(paymentItems.size() + "");

                byte[] recvTpsBuf = processThirdPartyServer(marshalTpsRequestMsg(tpsTia), "5000");
                tpsToa = unmarshalTpsResponseMsg(recvTpsBuf);
            } catch (SocketTimeoutException e) {
                logger.error("与第三方服务器通讯处理超时.", e);
                marshalAbnormalCbsResponse(TxnRtnCode.MSG_RECV_TIMEOUT, "与第三方服务器通讯处理超时", response);
                return;
            } catch (Exception e) {
                logger.error("与第三方服务器通讯处理异常.", e);
                marshalAbnormalCbsResponse(TxnRtnCode.MSG_COMM_ERROR, "与第三方服务器通讯处理异常", response);
                return;
            }

            //处理第三方响应报文
            if ("0".equals(tpsToa.getRtnCode())) { //对账成功
                try {
                    processTxn(paymentInfo, request);
                } catch (Exception e) {
                    marshalAbnormalCbsResponse(TxnRtnCode.TXN_EXECUTE_FAILED, e.getMessage(), response);
                    logger.error("业务处理失败.", e);
                }
                succCnt++;
                succAmt = succAmt.add(paymentInfo.getPayAmt());
            } else {  //处理TPS返回错误码
                String errmsg = getTpsRtnErrorMsg(tpsToa.getRtnCode());
                CbsToa5000Item cbsToaItem = new CbsToa5000Item();
                FbiBeanUtils.copyProperties(paymentInfo, cbsToaItem);
                cbsToaItem.setRtnMsg("[" + tpsToa.getRtnCode() + "]" + errmsg);
                cbsToaItems.add(cbsToaItem);

                result = false;
                failCnt++;
                failAmt = failAmt.add(paymentInfo.getPayAmt());
            }
        }

        CbsToa5000 cbsToa = new CbsToa5000();
        if (result) {
            cbsToa.setRtnCode("0000");
            cbsToa.setRtnMsg("对账成功，总笔数:[" + totalCnt + "] 总金额:[" + totalAmt.toString() + "]");
        } else {
            cbsToa.setRtnCode("1000");
            cbsToa.setRtnMsg("对账失败，成功笔数:[" + succCnt + "] 成功金额:[" + succAmt.toString() + "]，失败笔数:[" + failCnt + "] 失败金额:[" + failAmt.toString() + "]");
            cbsToa.setItemNum(cbsToaItems.size()+"");
            cbsToa.setItems(cbsToaItems);
        }

        //特色平台响应
        String cbsRespMsg = marshalCbsResponseMsg(cbsToa);
        response.setHeader("rtnCode", TxnRtnCode.TXN_EXECUTE_SECCESS.getCode());
        response.setResponseBody(cbsRespMsg.getBytes(response.getCharacterEncoding()));
    }

    //解包生成CBS请求报文BEAN
    private CbsTia5000 unmarshalCbsRequestMsg(byte[] body) throws Exception {
        CbsTia5000 tia = new CbsTia5000();
        SeperatedTextDataFormat dataFormat = new SeperatedTextDataFormat(tia.getClass().getPackage().getName());
        tia = (CbsTia5000) dataFormat.fromMessage(new String(body, "GBK"), "CbsTia5000");
        return tia;
    }

    //根据本地数据库中的已保存信息生成CBS响应报文
    private String generateCbsRespMsgByLocalDbInfo(FsHdPaymentInfo paymentInfo, List<FsHdPaymentItem> paymentItems) {
        CbsToa5000 cbsToa = new CbsToa5000();
        FbiBeanUtils.copyProperties(paymentInfo, cbsToa);

        List<CbsToa5000Item> cbsToaItems = new ArrayList<>();
        for (FsHdPaymentItem paymentItem : paymentItems) {
            CbsToa5000Item cbsToaItem = new CbsToa5000Item();
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
    private byte[] marshalTpsRequestMsg(TpsTia5000 tpsTia) {
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
    private TpsToa5000 unmarshalTpsResponseMsg(byte[] response) throws Exception {
        TpsToa5000 toa = new TpsToa5000();
        FixedLengthTextDataFormat dataFormat = new FixedLengthTextDataFormat(toa.getClass().getPackage().getName());
        toa = (TpsToa5000) dataFormat.fromMessage(response, "TpsToa5000");

        return toa;
    }

    //根据第三方服务器响应报文生成特色平台响应报文
    private String marshalCbsResponseMsg(CbsToa5000 cbsToa) {
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
    private List<FsHdPaymentInfo> selectNotCanceledPaymentInfosFromDB(String startDate, String endDate) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        FsHdPaymentInfoMapper mapper;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            mapper = session.getMapper(FsHdPaymentInfoMapper.class);
            FsHdPaymentInfoExample example = new FsHdPaymentInfoExample();
            example.createCriteria()
                    .andNotifyDateBetween(startDate, endDate)
                    .andLnkBillStatusNotEqualTo(BillStatus.CANCELED.getCode());
            return mapper.selectByExample(example);
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


    private void processTxn(FsHdPaymentInfo paymentInfo, Stdp10ProcessorRequest request) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            paymentInfo.setFbChkFlag("1");
            FsHdPaymentInfoMapper infoMapper = session.getMapper(FsHdPaymentInfoMapper.class);
            infoMapper.updateByPrimaryKey(paymentInfo);
            session.commit();
        } catch (Exception e) {
            session.rollback();
            throw new RuntimeException("业务逻辑处理失败。", e);
        } finally {
            session.close();
        }
    }
}

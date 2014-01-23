package org.fbi.fshd.processor;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.fbi.fshd.domain.cbs.T3010Request.CbsTia3010;
import org.fbi.fshd.domain.tps.T3010Request.TpsTia3010;
import org.fbi.fshd.domain.tps.T3010Response.TpsToa3010;
import org.fbi.fshd.enums.BillStatus;
import org.fbi.fshd.enums.TxnRtnCode;
import org.fbi.fshd.helper.FbiBeanUtils;
import org.fbi.fshd.helper.MybatisFactory;
import org.fbi.fshd.helper.ProjectConfigManager;
import org.fbi.fshd.repository.dao.FsHdPaymentInfoMapper;
import org.fbi.fshd.repository.model.FsHdPaymentInfo;
import org.fbi.fshd.repository.model.FsHdPaymentInfoExample;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanrui on 14-1-20.
 * 汇总通知书输出模式 缴款确认交易
 */
public class T3010Processor extends AbstractTxnProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        CbsTia3010 cbsTia;
        try {
            cbsTia = unmarshalCbsRequestMsg(request.getRequestBody());
        } catch (Exception e) {
            logger.error("特色业务平台请求报文解析错误.", e);
            marshalAbnormalCbsResponse(TxnRtnCode.CBSMSG_UNMARSHAL_FAILED, null, response);
            return;
        }

        //检查本地数据库信息
        FsHdPaymentInfo paymentInfo_db = selectNotCanceledPaymentInfoFromDB(cbsTia.getBillId());
        if (paymentInfo_db == null) {
            marshalAbnormalCbsResponse(TxnRtnCode.TXN_EXECUTE_FAILED, "请先做查询交易.", response);
            return;
        } else {
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
        TpsTia3010 tpsTia = new TpsTia3010();
        TpsToa3010 tpsToa;

        try {
            FbiBeanUtils.copyProperties(cbsTia, tpsTia);
            tpsTia.setFisCode(ProjectConfigManager.getInstance().getProperty("tps.fis.fiscode"));
            tpsTia.setTxnHdlCode("A");   //处理码 内容：A—表示请求收款、请求保存
            tpsTia.setFisActno(ProjectConfigManager.getInstance().getProperty("tps.fis.actno")); //财政专户账号
            //tpsTia.setVoucherType("01");     //通知书类型
            //tpsTia.setFisBatchSn("000001");   //批次号码信息
            tpsTia.setOutModeFlag("O");     //输出模式标识
            tpsTia.setBranchId(request.getHeader("branchId"));
            tpsTia.setTlrId(request.getHeader("tellerId"));

            byte[] recvTpsBuf = processThirdPartyServer(marshalTpsRequestMsg(tpsTia), "3010");
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
                processTxn(cbsTia, paymentInfo_db, request);
                marshalSuccessTxnCbsResponse(response);
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
    private CbsTia3010 unmarshalCbsRequestMsg(byte[] body) throws Exception {
        CbsTia3010 tia = new CbsTia3010();
        SeperatedTextDataFormat dataFormat = new SeperatedTextDataFormat(tia.getClass().getPackage().getName());
        tia = (CbsTia3010) dataFormat.fromMessage(new String(body, "GBK"), "CbsTia3010");
        return tia;
    }

    //组第三方服务器请求报文
    private byte[] marshalTpsRequestMsg(TpsTia3010 tpsTia) {
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
    private TpsToa3010 unmarshalTpsResponseMsg(byte[] response) throws Exception {
        TpsToa3010 toa = new TpsToa3010();
        FixedLengthTextDataFormat dataFormat = new FixedLengthTextDataFormat(toa.getClass().getPackage().getName());
        toa = (TpsToa3010) dataFormat.fromMessage(response, "TpsToa3010");

        return toa;
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

    private void processTxn(CbsTia3010 cbsTia, FsHdPaymentInfo paymentInfo, Stdp10ProcessorRequest request) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(request.getHeader("txnTime"));
            paymentInfo.setBankindate(new SimpleDateFormat("yyyyMMdd").format(date));

            //缴款相关信息由特色系统请求报文中提供
            paymentInfo.setVoucherType(cbsTia.getVoucherType());
            paymentInfo.setPayAmt(cbsTia.getPayAmt());

            paymentInfo.setOperPayBankid(request.getHeader("branchId"));
            paymentInfo.setOperPayTlrid(request.getHeader("tellerId"));
            paymentInfo.setOperPayDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            paymentInfo.setOperPayTime(new SimpleDateFormat("HHmmss").format(new Date()));
            paymentInfo.setOperPayHostsn(request.getHeader("serialNo"));

            paymentInfo.setHostBookFlag("1");
            paymentInfo.setHostChkFlag("0");
            paymentInfo.setFbBookFlag("1");
            paymentInfo.setFbChkFlag("0");

            paymentInfo.setHostAckFlag("0");
            paymentInfo.setLnkBillStatus(BillStatus.PAYOFF.getCode()); //已缴款
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

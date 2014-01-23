package org.fbi.fshd.processor;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.fbi.fshd.domain.cbs.T2090Request.CbsTia2090;
import org.fbi.fshd.domain.tps.T2090Request.TpsTia2090;
import org.fbi.fshd.domain.tps.T2090Response.TpsToa2090;
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
 * 手工票冲正交易
 */
public class T2090Processor extends AbstractTxnProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        CbsTia2090 cbsTia;
        try {
            cbsTia = unmarshalCbsRequestMsg(request.getRequestBody());
        } catch (Exception e) {
            logger.error("特色业务平台请求报文解析错误.", e);
            marshalAbnormalCbsResponse(TxnRtnCode.CBSMSG_UNMARSHAL_FAILED, null, response);
            return;
        }

        //检查本地数据库信息
        FsHdPaymentInfo paymentInfo_db = selectPayoffPaymentInfoFromDB(cbsTia.getFisBizId());
        if (paymentInfo_db == null) {
            marshalAbnormalCbsResponse(TxnRtnCode.TXN_EXECUTE_FAILED, "不存在已缴款的记录.", response);
            return;
        }

        //第三方通讯处理
        TpsTia2090 tpsTia = new TpsTia2090();
        TpsToa2090 tpsToa;

        try {
            FbiBeanUtils.copyProperties(cbsTia, tpsTia);
            tpsTia.setFisCode(ProjectConfigManager.getInstance().getProperty("tps.fis.fiscode"));
            tpsTia.setTxnHdlCode("4");   //处理码 内容：4—表示红冲信息
            tpsTia.setFisActno(ProjectConfigManager.getInstance().getProperty("tps.fis.actno"));  //财政专户账号
            //tpsTia.setVoucherType("01");     //通知书类型
            tpsTia.setBranchId(request.getHeader("branchId"));
            tpsTia.setTlrId(request.getHeader("tellerId"));
            tpsTia.setInstCode(paymentInfo_db.getInstCode());    //单位代码

            byte[] recvTpsBuf = processThirdPartyServer(marshalTpsRequestMsg(tpsTia), "2090");
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
                processTxn(paymentInfo_db, request);
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
    private CbsTia2090 unmarshalCbsRequestMsg(byte[] body) throws Exception {
        CbsTia2090 tia = new CbsTia2090();
        SeperatedTextDataFormat dataFormat = new SeperatedTextDataFormat(tia.getClass().getPackage().getName());
        tia = (CbsTia2090) dataFormat.fromMessage(new String(body, "GBK"), "CbsTia2090");
        return tia;
    }

    //组第三方服务器请求报文
    private byte[] marshalTpsRequestMsg(TpsTia2090 tpsTia) {
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
    private TpsToa2090 unmarshalTpsResponseMsg(byte[] response) throws Exception {
        TpsToa2090 toa = new TpsToa2090();
        FixedLengthTextDataFormat dataFormat = new FixedLengthTextDataFormat(toa.getClass().getPackage().getName());
        toa = (TpsToa2090) dataFormat.fromMessage(response, "TpsToa2090");

        return toa;
    }


    //=======数据库处理=================================================
    //查找已缴款未撤销的缴款单记录
    private FsHdPaymentInfo selectPayoffPaymentInfoFromDB(String fisBizId) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        FsHdPaymentInfoMapper mapper;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            mapper = session.getMapper(FsHdPaymentInfoMapper.class);
            FsHdPaymentInfoExample example = new FsHdPaymentInfoExample();
            example.createCriteria()
                    .andFisBizIdEqualTo(fisBizId)
                    .andLnkBillStatusEqualTo(BillStatus.PAYOFF.getCode());
            List<FsHdPaymentInfo> infos = mapper.selectByExample(example);
            if (infos.size() == 0) {
                return null;
            }
            if (infos.size() != 1) { //同一个缴款单号，已缴款未撤销的在表中只能存在一条记录
                throw new RuntimeException("记录状态错误.");
            }
            return infos.get(0);
        }
    }

    private void processTxn(FsHdPaymentInfo paymentInfo, Stdp10ProcessorRequest request) {
        SqlSessionFactory sqlSessionFactory = MybatisFactory.ORACLE.getInstance();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(request.getHeader("txnTime"));
            paymentInfo.setCanceldate(new SimpleDateFormat("yyyyMMdd").format(date));

            paymentInfo.setOperCancelBankid(request.getHeader("branchId"));
            paymentInfo.setOperCancelTlrid(request.getHeader("tellerId"));
            paymentInfo.setOperCancelDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            paymentInfo.setOperCancelTime(new SimpleDateFormat("HHmmss").format(new Date()));
            paymentInfo.setOperCancelHostsn(request.getHeader("serialNo"));

            paymentInfo.setLnkBillStatus(BillStatus.CANCELED.getCode()); //已撤销
            paymentInfo.setOperCancelHostsn(request.getHeader("serialNo")); //记录撤销交易的主机流水号

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

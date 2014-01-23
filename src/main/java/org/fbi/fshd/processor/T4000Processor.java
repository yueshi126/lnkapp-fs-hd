package org.fbi.fshd.processor;

import org.fbi.fshd.domain.cbs.T4000Request.CbsTia4000;
import org.fbi.fshd.domain.cbs.T4000Response.CbsToa4000;
import org.fbi.fshd.domain.tps.T4000Request.TpsTia4000;
import org.fbi.fshd.domain.tps.T4000Response.TpsToa4000;
import org.fbi.fshd.enums.TxnRtnCode;
import org.fbi.fshd.helper.FbiBeanUtils;
import org.fbi.fshd.helper.ProjectConfigManager;
import org.fbi.linking.codec.dataformat.FixedLengthTextDataFormat;
import org.fbi.linking.codec.dataformat.SeperatedTextDataFormat;
import org.fbi.linking.processor.ProcessorException;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorRequest;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanrui on 14-1-20.
 * 银行录入待查信息查询
 */
public class T4000Processor extends AbstractTxnProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        CbsTia4000 cbsTia;
        try {
            cbsTia = unmarshalCbsRequestMsg(request.getRequestBody());
        } catch (Exception e) {
            logger.error("特色业务平台请求报文解析错误.", e);
            marshalAbnormalCbsResponse(TxnRtnCode.CBSMSG_UNMARSHAL_FAILED, null, response);
            return;
        }

        //第三方通讯处理
        TpsTia4000 tpsTia = new TpsTia4000();
        TpsToa4000 tpsToa;

        try {
            FbiBeanUtils.copyProperties(cbsTia, tpsTia);
            tpsTia.setFisCode(ProjectConfigManager.getInstance().getProperty("tps.fis.fiscode"));
            tpsTia.setTxnHdlCode("G");   //处理码 内容：G—表示请求验证
            tpsTia.setFisActno(ProjectConfigManager.getInstance().getProperty("tps.fis.actno")); //财政专户账号

            byte[] recvTpsBuf = processThirdPartyServer(marshalTpsRequestMsg(tpsTia), "4000");
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

        //特色平台响应
        response.setHeader("rtnCode", TxnRtnCode.TXN_EXECUTE_SECCESS.getCode());
        String cbsRespMsg = marshalCbsResponseMsg(tpsToa);
        response.setResponseBody(cbsRespMsg.getBytes(response.getCharacterEncoding()));
    }

    //解包生成CBS请求报文BEAN
    private CbsTia4000 unmarshalCbsRequestMsg(byte[] body) throws Exception {
        CbsTia4000 tia = new CbsTia4000();
        SeperatedTextDataFormat dataFormat = new SeperatedTextDataFormat(tia.getClass().getPackage().getName());
        tia = (CbsTia4000) dataFormat.fromMessage(new String(body, "GBK"), "CbsTia4000");
        return tia;
    }

    //组第三方服务器请求报文
    private byte[] marshalTpsRequestMsg(TpsTia4000 tpsTia) {
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
    private TpsToa4000 unmarshalTpsResponseMsg(byte[] response) throws Exception {
        TpsToa4000 toa = new TpsToa4000();
        FixedLengthTextDataFormat dataFormat = new FixedLengthTextDataFormat(toa.getClass().getPackage().getName());
        toa = (TpsToa4000) dataFormat.fromMessage(response, "TpsToa4000");

        return toa;
    }

    //根据第三方服务器响应报文生成特色平台响应报文
    private String marshalCbsResponseMsg(TpsToa4000 tpsToa) {
        CbsToa4000 cbsToa = new CbsToa4000();
        FbiBeanUtils.copyProperties(tpsToa, cbsToa);
        cbsToa.setRtnMsg(getTpsRtnErrorMsg(tpsToa.getRtnCode()));

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

}

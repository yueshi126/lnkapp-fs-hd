package org.fbi.fshd.processor;

import org.apache.commons.lang.StringUtils;
import org.fbi.fshd.domain.cbs.T9999Response.TOA9999;
import org.fbi.fshd.enums.TxnRtnCode;
import org.fbi.fshd.helper.ProjectConfigManager;
import org.fbi.fshd.helper.TpsSocketClient;
import org.fbi.fshd.internal.AppActivator;
import org.fbi.linking.codec.dataformat.SeperatedTextDataFormat;
import org.fbi.linking.processor.ProcessorException;
import org.fbi.linking.processor.standprotocol10.Stdp10Processor;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorRequest;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorResponse;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: zhanrui
 * Date: 2014-1-18
 */
public abstract class AbstractTxnProcessor extends Stdp10Processor {
    protected static String CONTEXT_TPS_AUTHCODE = "CONTEXT_TPS_AUTHCODE";
    protected static String TPS_ENCODING = "GBK";  //第三方服务器编码方式
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //protected static String tps_authcode = "";

    @Override
    public void service(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        String txnCode = request.getHeader("txnCode");
        String tellerId = request.getHeader("tellerId");
        if (StringUtils.isEmpty(tellerId)) {
            tellerId = "TELLERID";
        }

        try {
            MDC.put("txnCode", txnCode);
            MDC.put("tellerId", tellerId);
            logger.info("CBS Request:" + request.toString());
            doRequest(request, response);
            logger.info("CBS Response:" + response.toString());
        }catch (Exception e){
            response.setHeader("rtnCode", TxnRtnCode.TXN_EXECUTE_FAILED.getCode());
            throw new RuntimeException(e);
        } finally {
            MDC.remove("txnCode");
            MDC.remove("tellerId");
        }
    }

    abstract protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException;

    //打包cbs交易成功报文
    protected void marshalSuccessTxnCbsResponse(Stdp10ProcessorResponse response) {
        String msg = "交易成功";
        try {
            response.setHeader("rtnCode", TxnRtnCode.TXN_EXECUTE_SECCESS.getCode());
            response.setResponseBody(msg.getBytes(response.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("编码错误", e);
        }
    }
    //打包cbs异常报文
    protected void marshalAbnormalCbsResponse(TxnRtnCode txnRtnCode, String errMsg, Stdp10ProcessorResponse response) {
        if (errMsg == null) {
            errMsg = txnRtnCode.getTitle();
        }
        String msg = getErrorRespMsgForStarring(errMsg);
        response.setHeader("rtnCode", txnRtnCode.getCode());
        try {
            response.setResponseBody(msg.getBytes(response.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("编码错误", e);
        }
    }

    //组统一的错误响应报文 txtMsg
    private String getErrorRespMsgForStarring(String errMsg) {
        TOA9999 toa = new TOA9999();
        toa.setErrMsg(errMsg);
        String starringRespMsg;
        Map<String, Object> modelObjectsMap = new HashMap<String, Object>();
        modelObjectsMap.put(toa.getClass().getName(), toa);
        SeperatedTextDataFormat starringDataFormat = new SeperatedTextDataFormat(toa.getClass().getPackage().getName());
        try {
            starringRespMsg = (String) starringDataFormat.toMessage(modelObjectsMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return starringRespMsg;
    }

    //根据第三方服务器的返回码从配置文件中获取对应的信息
    protected String getTpsRtnErrorMsg(String rtnCode) {
        BundleContext bundleContext = AppActivator.getBundleContext();
        URL url = bundleContext.getBundle().getEntry("rtncode.properties");

        Properties props = new Properties();
        try {
            props.load(url.openConnection().getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("错误码配置文件解析错误", e);
        }
        String property = props.getProperty(rtnCode);
        if (property == null) {
            property = "未定义对应的错误信息(错误码:" + rtnCode + ")";
        }
        return property;
    }

    //第三方服务器通讯报文头的长度处理
    protected String generateTpsRequestHeader(String sendMsg) throws UnsupportedEncodingException {
        String lenField = "" + (sendMsg.getBytes(TPS_ENCODING).length + 4);
        String rpad = "";
        for (int i = 0; i < 3 - lenField.length(); i++) {
            rpad += " ";
        }
        lenField = lenField + rpad;
        sendMsg = "0" + lenField + sendMsg;
        return sendMsg;
    }



    //第三方服务处理：可根据交易号设置不同的超时时间
    protected byte[] processThirdPartyServer(byte[] sendTpsBuf, String txnCode) throws Exception {
        String servIp = ProjectConfigManager.getInstance().getProperty("tps.server.ip");
        int servPort = Integer.parseInt(ProjectConfigManager.getInstance().getProperty("tps.server.port"));
        TpsSocketClient client = new TpsSocketClient(servIp, servPort);

        String timeoutCfg = ProjectConfigManager.getInstance().getProperty("tps.server.timeout.txn." + txnCode);
        if (timeoutCfg != null) {
            int timeout = Integer.parseInt(timeoutCfg);
            client.setTimeout(timeout);
        } else {
            timeoutCfg = ProjectConfigManager.getInstance().getProperty("tps.server.timeout");
            if (timeoutCfg != null) {
                int timeout = Integer.parseInt(timeoutCfg);
                client.setTimeout(timeout);
            }
        }

        logger.info("TPS Request:" + new String(sendTpsBuf, TPS_ENCODING));
        byte[] rcvTpsBuf = client.call(sendTpsBuf);
        logger.info("TPS Response:" + new String(rcvTpsBuf, TPS_ENCODING));
        return rcvTpsBuf;
    }

}

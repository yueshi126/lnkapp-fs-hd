package org.fbi.fshd.processor;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.fbi.fshd.domain.cbs.T6000Request.CbsTia6000;
import org.fbi.fshd.domain.cbs.T6000Response.CbsToa6000;
import org.fbi.fshd.domain.cbs.T6000Response.CbsToa6000Item;
import org.fbi.fshd.enums.BillStatus;
import org.fbi.fshd.enums.TxnRtnCode;
import org.fbi.fshd.helper.FbiBeanUtils;
import org.fbi.fshd.helper.MybatisFactory;
import org.fbi.fshd.repository.dao.FsHdPaymentInfoMapper;
import org.fbi.fshd.repository.dao.FsHdPaymentItemMapper;
import org.fbi.fshd.repository.model.FsHdPaymentInfo;
import org.fbi.fshd.repository.model.FsHdPaymentInfoExample;
import org.fbi.fshd.repository.model.FsHdPaymentItem;
import org.fbi.fshd.repository.model.FsHdPaymentItemExample;
import org.fbi.linking.codec.dataformat.SeperatedTextDataFormat;
import org.fbi.linking.processor.ProcessorException;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorRequest;
import org.fbi.linking.processor.standprotocol10.Stdp10ProcessorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanrui on 14-1-24.
 * 日报交易
 */
public class T6000Processor extends AbstractTxnProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doRequest(Stdp10ProcessorRequest request, Stdp10ProcessorResponse response) throws ProcessorException, IOException {
        CbsTia6000 cbsTia;
        try {
            cbsTia = unmarshalCbsRequestMsg(request.getRequestBody());
        } catch (Exception e) {
            logger.error("特色业务平台请求报文解析错误.", e);
            marshalAbnormalCbsResponse(TxnRtnCode.CBSMSG_UNMARSHAL_FAILED, null, response);
            return;
        }

        //本地数据库信息
        List<FsHdPaymentInfo> paymentInfos = selectNotCanceledPaymentInfosFromDB(cbsTia.getStratDate(), cbsTia.getEndDate());
        List<CbsToa6000Item> cbsToaItems = new ArrayList<>();

        int totalCnt = 0;
        BigDecimal totalAmt = new BigDecimal("0.00");

        for (FsHdPaymentInfo paymentInfo : paymentInfos) {
            totalCnt++;
            totalAmt = totalAmt.add(paymentInfo.getPayAmt());
            //List<FsHdPaymentItem> paymentItems = selectPaymentItemsFromDB(paymentInfo);
            CbsToa6000Item cbsToaItem = new CbsToa6000Item();
            FbiBeanUtils.copyProperties(paymentInfo, cbsToaItem);
            cbsToaItems.add(cbsToaItem);
        }

        CbsToa6000 cbsToa = new CbsToa6000();
        cbsToa.setRtnCode("0000");
        cbsToa.setRtnMsg("总笔数:[" + totalCnt + "] 总金额:[" + totalAmt.toString() + "]");
        cbsToa.setItemNum(cbsToaItems.size()+"");
        cbsToa.setItems(cbsToaItems);

        //特色平台响应
        String cbsRespMsg = marshalCbsResponseMsg(cbsToa);
        response.setHeader("rtnCode", TxnRtnCode.TXN_EXECUTE_SECCESS.getCode());
        response.setResponseBody(cbsRespMsg.getBytes(response.getCharacterEncoding()));
    }

    //解包生成CBS请求报文BEAN
    private CbsTia6000 unmarshalCbsRequestMsg(byte[] body) throws Exception {
        CbsTia6000 tia = new CbsTia6000();
        SeperatedTextDataFormat dataFormat = new SeperatedTextDataFormat(tia.getClass().getPackage().getName());
        tia = (CbsTia6000) dataFormat.fromMessage(new String(body, "GBK"), "CbsTia6000");
        return tia;
    }

    //根据第三方服务器响应报文生成特色平台响应报文
    private String marshalCbsResponseMsg(CbsToa6000 cbsToa) {
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
//                    .andNotifyDateBetween(startDate, endDate)
                    .andBankindateBetween(startDate, endDate)
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

}

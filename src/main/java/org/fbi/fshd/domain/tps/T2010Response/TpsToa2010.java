package org.fbi.fshd.domain.tps.T2010Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;

@FixedLengthTextMessage(mainClass = true)
public class TpsToa2010 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 1)
    private String rtnCode;               //验证码
    @DataField(seq = 4, length = 12)
    private String billId;                //缴款通知书号
    @DataField(seq = 5, length = 1)
    private String outModeFlag;           //输出模式标识


    public String getFisCode() {
        return fisCode;
    }

    public void setFisCode(String fisCode) {
        this.fisCode = fisCode;
    }

    public String getTxnHdlCode() {
        return txnHdlCode;
    }

    public void setTxnHdlCode(String txnHdlCode) {
        this.txnHdlCode = txnHdlCode;
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getOutModeFlag() {
        return outModeFlag;
    }

    public void setOutModeFlag(String outModeFlag) {
        this.outModeFlag = outModeFlag;
    }

    @Override
    public String toString() {
        return "TpsToa1010{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", rtnCode='" + rtnCode + '\'' +
                ", billId='" + billId + '\'' +
                ", outModeFlag='" + outModeFlag + '\'' +
                '}';
    }
}
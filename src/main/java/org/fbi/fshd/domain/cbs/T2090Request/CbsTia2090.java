package org.fbi.fshd.domain.cbs.T2090Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.math.BigDecimal;

@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia2090 {
    @DataField(seq = 1)
    private String billId;        //缴款通知书号
    @DataField(seq = 2)
    private String instCode;      //单位代码
    @DataField(seq = 3)
    private BigDecimal payAmt;    //总金额

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    @Override
    public String toString() {
        return "CbsTia2090{" +
                "billId='" + billId + '\'' +
                ", instCode='" + instCode + '\'' +
                ", payAmt=" + payAmt +
                '}';
    }
}
package org.fbi.fshd.domain.cbs.T3090Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.math.BigDecimal;

@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia3090 {
    @DataField(seq = 1)
    private String billId;

    @DataField(seq = 2)
    private String voucherType;    //票据类型

    @DataField(seq = 3)
    private BigDecimal payAmt;

    @DataField(seq = 4)
    private String fisBatchSn;            //批次号码信息

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getFisBatchSn() {
        return fisBatchSn;
    }

    public void setFisBatchSn(String fisBatchSn) {
        this.fisBatchSn = fisBatchSn;
    }

    @Override
    public String toString() {
        return "CbsTia3090{" +
                "billId='" + billId + '\'' +
                ", voucherType='" + voucherType + '\'' +
                ", payAmt=" + payAmt +
                ", fisBatchSn='" + fisBatchSn + '\'' +
                '}';
    }
}
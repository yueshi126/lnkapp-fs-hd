package org.fbi.fshd.domain.cbs.T1090Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.math.BigDecimal;

@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia1090 {
    @DataField(seq = 1)
    private String billId;

    @DataField(seq = 2)
    private String voucherType;    //∆±æ›¿‡–Õ

    @DataField(seq = 3)
    private BigDecimal payAmt;

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

    @Override
    public String toString() {
        return "CbsTia1010{" +
                "billId='" + billId + '\'' +
                ", voucherType='" + voucherType + '\'' +
                ", payAmt=" + payAmt +
                '}';
    }
}
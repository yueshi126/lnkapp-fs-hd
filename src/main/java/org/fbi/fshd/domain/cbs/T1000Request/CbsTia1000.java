package org.fbi.fshd.domain.cbs.T1000Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia1000 {
    @DataField(seq = 1)
    private String billId;
    @DataField(seq = 2)
    private String voucherType;           //∆±æ›¿‡–Õ

    public String getBillId() {
        return billId;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    @Override
    public String toString() {
        return "CbsTia1000{" +
                "billId='" + billId + '\'' +
                ", voucherType='" + voucherType + '\'' +
                '}';
    }
}
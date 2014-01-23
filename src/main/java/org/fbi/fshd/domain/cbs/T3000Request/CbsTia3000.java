package org.fbi.fshd.domain.cbs.T3000Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia3000 {
    @DataField(seq = 1)
    private String billId;
    @DataField(seq = 2)
    private String voucherType;           //票据类型
    @DataField(seq = 3)
    private String fisBatchSn;            //批次号码信息

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

    public String getFisBatchSn() {
        return fisBatchSn;
    }

    public void setFisBatchSn(String fisBatchSn) {
        this.fisBatchSn = fisBatchSn;
    }

    @Override
    public String toString() {
        return "CbsTia3000{" +
                "billId='" + billId + '\'' +
                ", voucherType='" + voucherType + '\'' +
                ", fisBatchSn='" + fisBatchSn + '\'' +
                '}';
    }
}
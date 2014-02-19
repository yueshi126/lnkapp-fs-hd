package org.fbi.fshd.domain.cbs.T6000Response;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManySeperatedTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManySeperatedTextMessage(separator = ",")
public class CbsToa6000Item {
    @DataField(seq = 1)
    private String notifyDate;
    @DataField(seq = 2)
    private String payerName;
    @DataField(seq = 3)
    private String billId;
    @DataField(seq = 4)
    private String fisBizId;
    @DataField(seq = 5)
    private BigDecimal payAmt;
    @DataField(seq = 6)
    private String rtnMsg;

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getFisBizId() {
        return fisBizId;
    }

    public void setFisBizId(String fisBizId) {
        this.fisBizId = fisBizId;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }

    @Override
    public String toString() {
        return "CbsToa6000Item{" +
                "notifyDate='" + notifyDate + '\'' +
                ", payerName='" + payerName + '\'' +
                ", billId='" + billId + '\'' +
                ", fisBizId='" + fisBizId + '\'' +
                ", payAmt=" + payAmt +
                ", rtnMsg='" + rtnMsg + '\'' +
                '}';
    }
}

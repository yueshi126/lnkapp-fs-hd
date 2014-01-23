package org.fbi.fshd.domain.tps.T4000Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@FixedLengthTextMessage(mainClass = true)
public class TpsTia4000 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 25)
    private String fisActno;              //财政专户帐号

    @DataField(seq = 4, length = 30)
    private String payerActno;    //缴款人账号
    @DataField(seq = 5, length = 30)
    private String payerName;     //缴款人
    @DataField(seq = 6, length = 250)
    private String remark;        //备注
    @DataField(seq = 7, length = 60)
    private String payerBank;     //开户银行
    @DataField(seq = 8, length = 12)
    private BigDecimal payAmt;    //金额
    @DataField(seq = 9, length = 8)
    private String notifyDate;    //通知日期  格式YYYYMMDD

    public String getPayerActno() {
        return payerActno;
    }

    public void setPayerActno(String payerActno) {
        this.payerActno = payerActno;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPayerBank() {
        return payerBank;
    }

    public void setPayerBank(String payerBank) {
        this.payerBank = payerBank;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

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

    public String getFisActno() {
        return fisActno;
    }

    public void setFisActno(String fisActno) {
        this.fisActno = fisActno;
    }

    @Override
    public String toString() {
        return "TpsTia4000{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", fisActno='" + fisActno + '\'' +
                ", payerActno='" + payerActno + '\'' +
                ", payerName='" + payerName + '\'' +
                ", remark='" + remark + '\'' +
                ", payerBank='" + payerBank + '\'' +
                ", payAmt=" + payAmt +
                ", notifyDate='" + notifyDate + '\'' +
                '}';
    }
}

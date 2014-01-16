package org.fbi.fshd.domain.cbs.T2010Request;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManySeperatedTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManySeperatedTextMessage(separator = ",")
public class CbsTia2010Item {
    @DataField(seq = 1)
    private String vchClass;     //票据种类   1—“收费收据”，2—“结算凭证”
    @DataField(seq = 2)
    private String vchNum;       //票据号
    @DataField(seq = 3)
    private BigDecimal txnAmt;    //开票金额
    @DataField(seq = 4)
    private String vchSts;        //票据状态   0—票据正确，1—票据作废


    public String getVchClass() {
        return vchClass;
    }

    public void setVchClass(String vchClass) {
        this.vchClass = vchClass;
    }

    public String getVchNum() {
        return vchNum;
    }

    public void setVchNum(String vchNum) {
        this.vchNum = vchNum;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getVchSts() {
        return vchSts;
    }

    public void setVchSts(String vchSts) {
        this.vchSts = vchSts;
    }

    @Override
    public String toString() {
        return "CbsTia2010Item{" +
                "vchClass='" + vchClass + '\'' +
                ", vchNum='" + vchNum + '\'' +
                ", txnAmt=" + txnAmt +
                ", vchSts='" + vchSts + '\'' +
                '}';
    }
}

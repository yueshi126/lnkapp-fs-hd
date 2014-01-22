package org.fbi.fshd.domain.tps.T2010Request;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManyFixedLengthTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManyFixedLengthTextMessage
public class TpsTia2010Item {
    @DataField(seq = 1, length = 1)
    private String vchClass;
    @DataField(seq = 2, length = 8)
    private String vchNum;
    @DataField(seq = 3, length = 12)
    private BigDecimal txnAmt;
    @DataField(seq = 4, length = 1)
    private String vchSts;

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
        return "TpsTia2010Item{" +
                "vchClass='" + vchClass + '\'' +
                ", vchNum='" + vchNum + '\'' +
                ", txnAmt=" + txnAmt +
                ", vchSts='" + vchSts + '\'' +
                '}';
    }
}

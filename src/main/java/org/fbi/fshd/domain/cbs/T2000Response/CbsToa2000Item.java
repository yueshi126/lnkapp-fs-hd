package org.fbi.fshd.domain.cbs.T2000Response;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManySeperatedTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManySeperatedTextMessage(separator = ",")
public class CbsToa2000Item {
    @DataField(seq = 1)
    private String prjCode;
    @DataField(seq = 2)
    private String measure;
    @DataField(seq = 3)
    private String handleNum;
    @DataField(seq = 4)
    private BigDecimal txnAmt;

    public String getPrjCode() {
        return prjCode;
    }

    public void setPrjCode(String prjCode) {
        this.prjCode = prjCode;
    }


    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getHandleNum() {
        return handleNum;
    }

    public void setHandleNum(String handleNum) {
        this.handleNum = handleNum;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    @Override
    public String toString() {
        return "CbsTia2000Item{" +
                "prjCode='" + prjCode + '\'' +
                ", measure='" + measure + '\'' +
                ", handleNum='" + handleNum + '\'' +
                ", txnAmt=" + txnAmt +
                '}';
    }
}

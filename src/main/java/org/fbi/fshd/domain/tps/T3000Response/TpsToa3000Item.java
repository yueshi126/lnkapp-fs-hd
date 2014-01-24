package org.fbi.fshd.domain.tps.T3000Response;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManyFixedLengthTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManyFixedLengthTextMessage
public class TpsToa3000Item {
    @DataField(seq = 1, length = 8)
    private String prjCode;    //项目代码
    @DataField(seq = 2, length = 30)
    private String prjName;    //项目代码
    @DataField(seq = 3, length = 6)
    private String measure;    //计量单位
    @DataField(seq = 4, length = 6)
    private String handleNum;  //计量数量
    @DataField(seq = 5, length = 12)
    private BigDecimal txnAmt;  //金额

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

    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    @Override
    public String toString() {
        return "TpsToa3000Item{" +
                "prjCode='" + prjCode + '\'' +
                ", prjName='" + prjName + '\'' +
                ", measure='" + measure + '\'' +
                ", handleNum='" + handleNum + '\'' +
                ", txnAmt=" + txnAmt +
                '}';
    }
}

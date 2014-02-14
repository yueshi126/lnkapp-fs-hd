package org.fbi.fshd.domain.tps.T5000Request;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManyFixedLengthTextMessage;

import java.math.BigDecimal;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManyFixedLengthTextMessage
public class TpsTia5000Item {
    @DataField(seq = 1, length = 6)
    private String prjCode;    //��Ŀ����
    @DataField(seq = 2, length = 6)
    private String handleNum;  //��������
    @DataField(seq = 3, length = 12)
    private BigDecimal txnAmt;  //���

    public String getPrjCode() {
        return prjCode;
    }

    public void setPrjCode(String prjCode) {
        this.prjCode = prjCode;
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
}
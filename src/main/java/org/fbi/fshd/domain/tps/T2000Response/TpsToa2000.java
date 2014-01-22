package org.fbi.fshd.domain.tps.T2000Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhanrui on 14-1-16.
 */
@FixedLengthTextMessage(mainClass = true)
public class TpsToa2000 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 1)
    private String rtnCode;               //验证码
    @DataField(seq = 4, length = 10)
    private String fisBizId;             //财政业务ID号
    @DataField(seq = 5, length = 30)
    private String instName;             //单位名称

    @DataField(seq = 6, length = 1)
    private String itemNum;

    @DataField(seq = 7, length = 32)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.tps.T2000Response.TpsToa2000Item", totalNumberField = "itemNum")
    private List<TpsToa2000Item> items;

    @DataField(seq = 8, length = 3)
    private String overdueRatio;  //滞纳金比例
    @DataField(seq = 9, length = 12)
    private BigDecimal overdueAmt;//滞纳金金额

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

    public String getFisBizId() {
        return fisBizId;
    }

    public void setFisBizId(String fisBizId) {
        this.fisBizId = fisBizId;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public List<TpsToa2000Item> getItems() {
        return items;
    }

    public void setItems(List<TpsToa2000Item> items) {
        this.items = items;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public String getOverdueRatio() {
        return overdueRatio;
    }

    public void setOverdueRatio(String overdueRatio) {
        this.overdueRatio = overdueRatio;
    }

    public BigDecimal getOverdueAmt() {
        return overdueAmt;
    }

    public void setOverdueAmt(BigDecimal overdueAmt) {
        this.overdueAmt = overdueAmt;
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    @Override
    public String toString() {
        return "TpsToa2000{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", rtnCode='" + rtnCode + '\'' +
                ", fisBizId='" + fisBizId + '\'' +
                ", instName='" + instName + '\'' +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                ", overdueRatio='" + overdueRatio + '\'' +
                ", overdueAmt=" + overdueAmt +
                '}';
    }
}

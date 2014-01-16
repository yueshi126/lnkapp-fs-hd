package org.fbi.fshd.domain.cbs.T2000Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsToa2000 {
    @DataField(seq = 1)
    private String fisBizId;      //财政业务ID号
    @DataField(seq = 2)
    private String instName;      //单位
    @DataField(seq = 3)
    private String prnVchTypNum;  //打印票据种类数

    @DataField(seq = 4)
    private String overdueRatio;  //滞纳金比例
    @DataField(seq = 5)
    private BigDecimal overdueAmt;//滞纳金金额

    @DataField(seq = 6)
    private String itemNum;

    @DataField(seq = 7)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.cbs.T2000Response.CbsToa2000Item", totalNumberField = "itemNum")
    private List<CbsToa2000Item> items;

    public String getFisBizId() {
        return fisBizId;
    }

    public void setFisBizId(String fisBizId) {
        this.fisBizId = fisBizId;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public String getPrnVchTypNum() {
        return prnVchTypNum;
    }

    public void setPrnVchTypNum(String prnVchTypNum) {
        this.prnVchTypNum = prnVchTypNum;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public List<CbsToa2000Item> getItems() {
        return items;
    }

    public void setItems(List<CbsToa2000Item> items) {
        this.items = items;
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

    @Override
    public String toString() {
        return "CbsToa2000{" +
                "fisBizId='" + fisBizId + '\'' +
                ", instName='" + instName + '\'' +
                ", prnVchTypNum='" + prnVchTypNum + '\'' +
                ", overdueRatio='" + overdueRatio + '\'' +
                ", overdueAmt=" + overdueAmt +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                '}';
    }
}

package org.fbi.fshd.domain.cbs.T1000Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsToa1000 {
    @DataField(seq = 1)
    private String billId;        //缴款通知书号
    @DataField(seq = 2)
    private String instCode;      //单位代码
    @DataField(seq = 3)
    private String payerName;     //缴款人
    @DataField(seq = 4)
    private String notifyDate;    //通知日期
    @DataField(seq = 5)
    private String latestDate;    //最迟日期
    @DataField(seq = 6)
    private String overdueRatio;  //滞纳金比例
    @DataField(seq = 7)
    private BigDecimal overdueAmt;//滞纳金金额
    @DataField(seq = 8)
    private String verifyNo;      //校验码

    //private String payerActno;    //缴款人账号
    //private String payerBank;     //开户银行

    @DataField(seq = 9)
    private String itemNum;

    @DataField(seq = 10)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.cbs.T1000Response.CbsToa1000Item", totalNumberField = "itemNum")
    private java.util.List<CbsToa1000Item> items;

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(String latestDate) {
        this.latestDate = latestDate;
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

    public String getVerifyNo() {
        return verifyNo;
    }

    public void setVerifyNo(String verifyNo) {
        this.verifyNo = verifyNo;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public List<CbsToa1000Item> getItems() {
        return items;
    }

    public void setItems(List<CbsToa1000Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CbsToa1000{" +
                "billId='" + billId + '\'' +
                ", instCode='" + instCode + '\'' +
                ", payerName='" + payerName + '\'' +
                ", notifyDate='" + notifyDate + '\'' +
                ", latestDate='" + latestDate + '\'' +
                ", overdueRatio='" + overdueRatio + '\'' +
                ", overdueAmt=" + overdueAmt +
                ", verifyNo='" + verifyNo + '\'' +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                '}';
    }
}

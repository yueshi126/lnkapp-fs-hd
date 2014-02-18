package org.fbi.fshd.domain.tps.T3000Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;

import java.util.List;

@FixedLengthTextMessage(mainClass = true)
public class TpsToa3000 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 1)
    private String rtnCode;               //验证码
    @DataField(seq = 4, length = 25)
    private String fisActno;              //财政专户帐号
    @DataField(seq = 5, length = 12)
    private String billId;                //缴款通知书号
    @DataField(seq = 6, length = 7)
    private String instCode;              //单位代码
    @DataField(seq = 7, length = 30)
    private String payerName;              //缴款人

    @DataField(seq = 8, length = 1)
    private String itemNum;
    @DataField(seq = 9, length = 62)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.tps.T3000Response.TpsToa3000Item", totalNumberField = "itemNum")
    private List<TpsToa3000Item> items;

    @DataField(seq = 10, length = 8)
    private String notifyDate;              //通知日期
    @DataField(seq = 11, length = 8)
    private String latestDate;              //最迟日期
    @DataField(seq = 12, length = 3)
    private String overdueRatio;            //滞纳金比例
    @DataField(seq = 13, length = 12)
    private String overdueAmt;              //滞纳金金额
    @DataField(seq = 14, length = 5)
    private String verifyNo;                //校验码
/*
    @DataField(seq = 15, length = 30)
    private String payerActno;              //缴款人账号
    @DataField(seq = 16, length = 60)
    private String payerBank;               //缴款人开户银行
*/
    @DataField(seq = 15, length = 1)
    private String outModeFlag;           //输出模式标识


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

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getFisActno() {
        return fisActno;
    }

    public void setFisActno(String fisActno) {
        this.fisActno = fisActno;
    }

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

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public List<TpsToa3000Item> getItems() {
        return items;
    }

    public void setItems(List<TpsToa3000Item> items) {
        this.items = items;
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

    public String getOverdueAmt() {
        return overdueAmt;
    }

    public void setOverdueAmt(String overdueAmt) {
        this.overdueAmt = overdueAmt;
    }

    public String getVerifyNo() {
        return verifyNo;
    }

    public void setVerifyNo(String verifyNo) {
        this.verifyNo = verifyNo;
    }

/*
    public String getPayerActno() {
        return payerActno;
    }

    public void setPayerActno(String payerActno) {
        this.payerActno = payerActno;
    }

    public String getPayerBank() {
        return payerBank;
    }

    public void setPayerBank(String payerBank) {
        this.payerBank = payerBank;
    }
*/

    public String getOutModeFlag() {
        return outModeFlag;
    }

    public void setOutModeFlag(String outModeFlag) {
        this.outModeFlag = outModeFlag;
    }

    @Override
    public String toString() {
        return "TpsToa3000{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", rtnCode='" + rtnCode + '\'' +
                ", fisActno='" + fisActno + '\'' +
                ", billId='" + billId + '\'' +
                ", instCode='" + instCode + '\'' +
                ", payerName='" + payerName + '\'' +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                ", notifyDate='" + notifyDate + '\'' +
                ", latestDate='" + latestDate + '\'' +
                ", overdueRatio='" + overdueRatio + '\'' +
                ", overdueAmt='" + overdueAmt + '\'' +
                ", verifyNo='" + verifyNo + '\'' +
//                ", payerActno='" + payerActno + '\'' +
//                ", payerBank='" + payerBank + '\'' +
                ", outModeFlag='" + outModeFlag + '\'' +
                '}';
    }
}
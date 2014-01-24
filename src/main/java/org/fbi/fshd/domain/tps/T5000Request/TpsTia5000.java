package org.fbi.fshd.domain.tps.T5000Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;

import java.util.List;

@FixedLengthTextMessage(mainClass = true)
public class TpsTia5000 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 25)
    private String fisActno;              //财政专户帐号
    @DataField(seq = 4, length = 10)
    private String branchId;              //网点代码
    @DataField(seq = 5, length = 5)
    private String tlrId;                 //操作员代码
    @DataField(seq = 6, length = 10)
    private String fisBizId;              //财政业务ID号
    @DataField(seq = 7, length = 6)
    private String fisBatchSn;            //批次号码信息
    @DataField(seq = 8, length = 12)
    private String billId;                //缴款通知书号
    @DataField(seq = 9, length = 7)
    private String instCode;              //单位代码
    @DataField(seq = 10, length = 30)
    private String payerName;             //缴款人

    @DataField(seq = 11, length = 1)
    private String itemNum;
    @DataField(seq = 12, length = 30)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.tps.T5000Response.TpsTia5000Item", totalNumberField = "itemNum")
    private List<TpsTia5000Item> items;

    @DataField(seq = 13, length = 8)
    private String notifyDate;              //通知日期

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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId;
    }

    public String getFisBizId() {
        return fisBizId;
    }

    public void setFisBizId(String fisBizId) {
        this.fisBizId = fisBizId;
    }

    public String getFisBatchSn() {
        return fisBatchSn;
    }

    public void setFisBatchSn(String fisBatchSn) {
        this.fisBatchSn = fisBatchSn;
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

    public List<TpsTia5000Item> getItems() {
        return items;
    }

    public void setItems(List<TpsTia5000Item> items) {
        this.items = items;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

    @Override
    public String toString() {
        return "TpsTia5000{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", fisActno='" + fisActno + '\'' +
                ", branchId='" + branchId + '\'' +
                ", tlrId='" + tlrId + '\'' +
                ", fisBizId='" + fisBizId + '\'' +
                ", fisBatchSn='" + fisBatchSn + '\'' +
                ", billId='" + billId + '\'' +
                ", instCode='" + instCode + '\'' +
                ", payerName='" + payerName + '\'' +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                ", notifyDate='" + notifyDate + '\'' +
                '}';
    }
}
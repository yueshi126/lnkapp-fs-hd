package org.fbi.fshd.domain.cbs.T2000Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.util.List;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia2000 {
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
    private String verifyNo;      //校验码
    @DataField(seq = 7)
    private String remark;        //备注
    @DataField(seq = 8)
    private String voucherType;   //票据类型
    @DataField(seq = 9)
    private String billType;      //通知书类别
    @DataField(seq = 10)
    private String contractNo;    //合同号

    @DataField(seq = 11)
    private String itemNum;

    @DataField(seq = 12)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.cbs.T2000Response.CbsTia2000Item", totalNumberField = "itemNum")
    private List<CbsTia2000Item> items;

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

    public String getVerifyNo() {
        return verifyNo;
    }

    public void setVerifyNo(String verifyNo) {
        this.verifyNo = verifyNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public List<CbsTia2000Item> getItems() {
        return items;
    }

    public void setItems(List<CbsTia2000Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CbsTia2000{" +
                "billId='" + billId + '\'' +
                ", instCode='" + instCode + '\'' +
                ", payerName='" + payerName + '\'' +
                ", notifyDate='" + notifyDate + '\'' +
                ", latestDate='" + latestDate + '\'' +
                ", verifyNo='" + verifyNo + '\'' +
                ", remark='" + remark + '\'' +
                ", voucherType='" + voucherType + '\'' +
//                ", billType='" + billType + '\'' +
//                ", contractNo='" + contractNo + '\'' +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                '}';
    }
}

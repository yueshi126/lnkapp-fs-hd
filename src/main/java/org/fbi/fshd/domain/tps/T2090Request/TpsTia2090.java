package org.fbi.fshd.domain.tps.T2090Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;

import java.math.BigDecimal;

@FixedLengthTextMessage(mainClass = true)
public class TpsTia2090 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 25)
    private String fisActno;              //财政专户帐号
    @DataField(seq = 4, length = 10)
    private String branchId;             //网点代码
    @DataField(seq = 5, length = 5)
    private String tlrId;                //操作员代码
    @DataField(seq = 6, length = 7)
    private String instCode;                //单位代码
    @DataField(seq = 7, length = 2)
    private String billType;              //通知书类别 0:一般通知书；1：土地出让金类通知书；（出让金通知书参数启用，暂时没有）
    @DataField(seq = 8, length = 6)
    private String fisBatchSn;            //批次号码信息
    @DataField(seq = 9, length = 12)
    private String billId;                //缴款通知书号
    @DataField(seq = 10, length = 12)
    private BigDecimal payAmt;                 //总金额
    @DataField(seq = 11, length = 1)
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

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
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

    public String getFisActno() {
        return fisActno;
    }

    public void setFisActno(String fisActno) {
        this.fisActno = fisActno;
    }

    public String getOutModeFlag() {
        return outModeFlag;
    }

    public void setOutModeFlag(String outModeFlag) {
        this.outModeFlag = outModeFlag;
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

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    @Override
    public String toString() {
        return "TpsTia1090{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", fisActno='" + fisActno + '\'' +
                ", branchId='" + branchId + '\'' +
                ", tlrId='" + tlrId + '\'' +
                ", instCode='" + instCode + '\'' +
                ", billType='" + billType + '\'' +
                ", fisBatchSn='" + fisBatchSn + '\'' +
                ", billId='" + billId + '\'' +
                ", payAmt=" + payAmt +
                ", outModeFlag='" + outModeFlag + '\'' +
                '}';
    }
}
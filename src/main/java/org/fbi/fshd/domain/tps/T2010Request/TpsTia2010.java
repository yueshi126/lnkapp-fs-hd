package org.fbi.fshd.domain.tps.T2010Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.FixedLengthTextMessage;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;

import java.util.List;

/**
 * Created by zhanrui on 14-1-16.
 */
@FixedLengthTextMessage(mainClass = true)
public class TpsTia2010 {
    @DataField(seq = 1, length = 1)
    private String fisCode;               //财政局编码 4
    @DataField(seq = 2, length = 1)
    private String txnHdlCode;            //交易处理码
    @DataField(seq = 3, length = 10)
    private String fisBizId;             //财政业务ID号

    @DataField(seq = 4, length = 1)
    private String itemNum = "0";

   /* @DataField(seq = 5, length = 22)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.tps.T2010Request.TpsTia2010Item", totalNumberField = "itemNum")
    private List<TpsTia2010Item> items;*/

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

   /* public List<TpsTia2010Item> getItems() {
        return items;
    }

    public void setItems(List<TpsTia2010Item> items) {
        this.items = items;
    }*/

    @Override
    public String toString() {
        return "TpsTia2010{" +
                "fisCode='" + fisCode + '\'' +
                ", txnHdlCode='" + txnHdlCode + '\'' +
                ", fisBizId='" + fisBizId + '\'' +
                ", itemNum='" + itemNum + '\'' +
//                ", items=" + items +
                '}';
    }
}

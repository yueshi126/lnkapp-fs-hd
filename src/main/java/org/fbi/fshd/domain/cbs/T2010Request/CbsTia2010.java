package org.fbi.fshd.domain.cbs.T2010Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToMany;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

import java.util.List;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia2010 {
    @DataField(seq = 1)
    private String fisBizId;      //财政业务ID号
    @DataField(seq = 2)
    private String instName;      //单位

    @DataField(seq = 3)
    private String itemNum;

    @DataField(seq = 4)
    @OneToMany(mappedTo = "org.fbi.fshd.domain.cbs.T2000Response.CbsTia2010Item", totalNumberField = "itemNum")
    private List<CbsTia2010Item> items;

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

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public List<CbsTia2010Item> getItems() {
        return items;
    }

    public void setItems(List<CbsTia2010Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CbsTia2010{" +
                "fisBizId='" + fisBizId + '\'' +
                ", instName='" + instName + '\'' +
                ", itemNum='" + itemNum + '\'' +
                ", items=" + items +
                '}';
    }
}

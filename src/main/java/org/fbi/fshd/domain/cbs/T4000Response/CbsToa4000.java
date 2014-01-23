package org.fbi.fshd.domain.cbs.T4000Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsToa4000 {
    @DataField(seq = 1)
    private String rtnCode;      //财政返回码
    @DataField(seq = 2)
    private String rtnMsg;       //财政返回码对应的信息
    @DataField(seq = 3)
    private String fisBizId;     //财政业务ID号

    public String getFisBizId() {
        return fisBizId;
    }

    public void setFisBizId(String fisBizId) {
        this.fisBizId = fisBizId;
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }

    @Override
    public String toString() {
        return "CbsToa4000{" +
                "rtnCode='" + rtnCode + '\'' +
                ", rtnMsg='" + rtnMsg + '\'' +
                ", fisBizId='" + fisBizId + '\'' +
                '}';
    }
}

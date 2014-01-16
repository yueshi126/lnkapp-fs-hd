package org.fbi.fshd.domain.cbs.T2010Response;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsToa2010 {
    @DataField(seq = 1)
    private String fisBizId;      //财政业务ID号

    public String getFisBizId() {
        return fisBizId;
    }

    public void setFisBizId(String fisBizId) {
        this.fisBizId = fisBizId;
    }

    @Override
    public String toString() {
        return "CbsToa2010{" +
                "fisBizId='" + fisBizId + '\'' +
                '}';
    }
}

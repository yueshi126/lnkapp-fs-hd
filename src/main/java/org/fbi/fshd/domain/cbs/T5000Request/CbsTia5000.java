package org.fbi.fshd.domain.cbs.T5000Request;

import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.SeperatedTextMessage;

/**
 * Created by zhanrui on 14-1-16.
 */
@SeperatedTextMessage(separator = "\\|", mainClass = true)
public class CbsTia5000 {
    @DataField(seq = 1)
    private String stratDate;
    @DataField(seq = 2)
    private String endDate;

    public String getStratDate() {
        return stratDate;
    }

    public void setStratDate(String stratDate) {
        this.stratDate = stratDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "CbsTia5000{" +
                "stratDate='" + stratDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}

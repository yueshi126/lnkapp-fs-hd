package org.fbi.fshd.domain.tps.T2000Response;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManyFixedLengthTextMessage;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManyFixedLengthTextMessage
public class TpsToa2000Item {
    @DataField(seq = 1, length = 30)
    private String prjName;
    @DataField(seq = 2, length = 1)
    private String prjVerifyResult;
    @DataField(seq = 3, length = 1)
    private String vchClass;

    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    public String getPrjVerifyResult() {
        return prjVerifyResult;
    }

    public void setPrjVerifyResult(String prjVerifyResult) {
        this.prjVerifyResult = prjVerifyResult;
    }

    public String getVchClass() {
        return vchClass;
    }

    public void setVchClass(String vchClass) {
        this.vchClass = vchClass;
    }

    @Override
    public String toString() {
        return "TpsToa2000Item{" +
                "prjName='" + prjName + '\'' +
                ", prjVerifyResult='" + prjVerifyResult + '\'' +
                ", vchClass='" + vchClass + '\'' +
                '}';
    }
}

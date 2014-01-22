package org.fbi.fshd.domain.cbs.T2000Response;


import org.fbi.linking.codec.dataformat.annotation.DataField;
import org.fbi.linking.codec.dataformat.annotation.OneToManySeperatedTextMessage;

/**
 * Created by zhanrui on 14-1-16.
 */
@OneToManySeperatedTextMessage(separator = ",")
public class CbsToa2000Item {
    @DataField(seq = 1)
    private String prjName;
    @DataField(seq = 2)
    private String prjVerifyResult;
    @DataField(seq = 3)
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
        return "CbsToa2000Item{" +
                "prjName='" + prjName + '\'' +
                ", prjVerifyResult='" + prjVerifyResult + '\'' +
                ", vchClass='" + vchClass + '\'' +
                '}';
    }
}

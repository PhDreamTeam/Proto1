package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

public class MsgApplyMergeDDObjectReply extends MsgReply implements Serializable {
    String ddToMergeDDUI, newDDUI;
    int nDataElemsDD;


    public MsgApplyMergeDDObjectReply(String DDUI, String requestId, String ddToMergeDDUI,
                                      String newDDUI, int nDataElemsDD,
                                      boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);

        this.ddToMergeDDUI = ddToMergeDDUI;
        this.newDDUI = newDDUI;
        this.nDataElemsDD = nDataElemsDD;
    }


    public String getDdToMergeDDUI() {
        return ddToMergeDDUI;
    }

    public String getNewDDUI() {
        return newDDUI;
    }

    public int getnDataElemsDD() {
        return nDataElemsDD;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() +  ", ddToMergeDDUI: " + getDdToMergeDDUI()
                +  ", srcDDUI: " + getNewDDUI()+ ", nElems: " + getnDataElemsDD();
    }
}

package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

public class MsgApplyFilterDDObjectReply extends MsgReply implements Serializable {
    String newDDUI;
    int nDataElemsDD;


    public MsgApplyFilterDDObjectReply(String DDUI, String requestId, String newDDUI, int nDataElemsDD,
                                       boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);

        this.newDDUI = newDDUI;
        this.nDataElemsDD = nDataElemsDD;
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
        return super.getIntermediateInfo() +  ", srcDDUI: " + getNewDDUI()+ ", nElems: " + getnDataElemsDD();
    }
}

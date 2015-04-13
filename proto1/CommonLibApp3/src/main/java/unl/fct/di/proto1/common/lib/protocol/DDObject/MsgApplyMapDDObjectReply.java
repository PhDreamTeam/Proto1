package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

public class MsgApplyMapDDObjectReply extends MsgReply implements Serializable {
    String newDDUI;
    int nDataElemsDD;
    boolean hasIncompleteResults;

    public MsgApplyMapDDObjectReply(String DDUI, String requestId, String newDDUI, int nDataElemsDD,
                                    boolean hasIncompleteResults, boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.newDDUI = newDDUI;
        this.nDataElemsDD = nDataElemsDD;
        this.hasIncompleteResults = hasIncompleteResults;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public int getNDataElemsDD() {
        return nDataElemsDD;
    }

    public boolean hasIncompleteResults() {
        return hasIncompleteResults;
    }

    public void setIncompleteResults(String failureReason) {
        hasIncompleteResults = true;
        setFailureReason(failureReason);
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() +  ", newDDUI: " + getNewDDUI() +
                ", nElems: " + getNDataElemsDD() +
                (hasIncompleteResults ? " has incomplete results: " + getFailureReason() : "");
    }
}

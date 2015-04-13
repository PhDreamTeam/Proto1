package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;


public class MsgApplyMergeDDObject extends Msg implements Serializable {
    String ddToMergeDDUI, newDDUI;
    boolean allowIncompleteResults;

    public MsgApplyMergeDDObject(String DDUI, String requestId, String ddToMergeDDUI,
                                 String newDDUI, boolean allowIncompleteResults) {
        super(DDUI, requestId);
        this.ddToMergeDDUI = ddToMergeDDUI;
        this.newDDUI = newDDUI;
        this.allowIncompleteResults = allowIncompleteResults;
    }

    public String getDdToMergeDDUI() {
        return ddToMergeDDUI;
    }

    public String getNewDDUI() {
        return newDDUI;
    }

    public boolean allowIncompleteResults() {
        return allowIncompleteResults;
    }


    @Override
    public String toString() {
        return super.toString() + " ddToMergeDDUI: " + getDdToMergeDDUI() + " newDDUI: " + getNewDDUI() +
                (allowIncompleteResults ? " allows incomplete results" : "");
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyMergeDDObjectReply(getDDUI(), getRequestId(), ddToMergeDDUI, newDDUI, 0, false, false, failureReason);
    }

    public Msg getSuccessReplyMessage(int nElems) {
        return new MsgApplyMergeDDObjectReply(getDDUI(), getRequestId(), ddToMergeDDUI, newDDUI, nElems, false, true, null);
    }
}
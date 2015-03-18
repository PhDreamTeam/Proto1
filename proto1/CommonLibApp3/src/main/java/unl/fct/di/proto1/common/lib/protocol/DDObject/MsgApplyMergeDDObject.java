package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;


public class MsgApplyMergeDDObject extends Msg implements Serializable {
    String ddToMergeDDUI, newDDUI;

    public MsgApplyMergeDDObject(String DDUI, String requestId, String ddToMergeDDUI,
                                 String newDDUI) {
        super(DDUI, requestId);
        this.ddToMergeDDUI = ddToMergeDDUI;
        this.newDDUI = newDDUI;
    }

    public String getDdToMergeDDUI() {
        return ddToMergeDDUI;
    }

    public String getNewDDUI() {
        return newDDUI;
    }


    @Override
    public String toString() {
        return super.toString() + " ddToMergeDDUI: " + getDdToMergeDDUI() + " srcDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyMergeDDObjectReply(getDDUI(), getRequestId(), ddToMergeDDUI, newDDUI, 0, false, failureReason);
    }

    public Msg getSuccessReplyMessage(int nElems) {
        return new MsgApplyMergeDDObjectReply(getDDUI(), getRequestId(), ddToMergeDDUI, newDDUI, nElems, true, null);
    }
}
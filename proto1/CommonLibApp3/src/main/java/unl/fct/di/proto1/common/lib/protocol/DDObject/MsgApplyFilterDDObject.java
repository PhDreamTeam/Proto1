package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class MsgApplyFilterDDObject<T> extends Msg implements Serializable {
    String newDDUI;
    Predicate<T> filter;
    boolean allowIncompleteResults;

    public MsgApplyFilterDDObject(String DDUI, String requestId, String newDDUI,
                                  Predicate<T> filter,  boolean allowIncompleteResults) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.filter = filter;
        this.allowIncompleteResults = allowIncompleteResults;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Predicate<T> getFilter() {
        return filter;
    }

    public boolean allowIncompleteResults() {
        return allowIncompleteResults;
    }

    @Override
    public String toString() {
        return super.toString() + " srcDDUI: " + getNewDDUI() +
                (allowIncompleteResults ? " allows incomplete results" : "");
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFilterDDObjectReply(getDDUI(), getRequestId(), newDDUI, 0, false, false, failureReason);
    }

    public MsgApplyFilterDDObjectReply getSuccessReplyMessage(int nElems) {
        return new MsgApplyFilterDDObjectReply(getDDUI(), getRequestId(), newDDUI, nElems, false, true, null);
    }
}
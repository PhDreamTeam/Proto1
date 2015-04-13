package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;


public class MsgGetDataDDObject<T> extends Msg implements Serializable{
    boolean allowIncompleteResults;

    public MsgGetDataDDObject(String DDUI, String requestId, boolean allowIncompleteResults) {
        super(DDUI, requestId);
        this.allowIncompleteResults = allowIncompleteResults;
    }

    public boolean allowIncompleteResults() {
        return allowIncompleteResults;
    }

    @Override
    public String toString() {
        return super.toString() + (allowIncompleteResults ? " allows incomplete results" : "");
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgGetDataDDObjectReply<>(getDDUI(), getRequestId(), null, false, false, failureReason);
    }

    public MsgGetDataDDObjectReply<T> getSuccessReplyMessage(T[] data) {
        return new MsgGetDataDDObjectReply<>(getDDUI(), getRequestId(), data, false, true, null);
    }
}

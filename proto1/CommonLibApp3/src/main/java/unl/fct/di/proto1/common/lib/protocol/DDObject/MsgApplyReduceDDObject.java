package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Reduction;

import java.io.Serializable;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class MsgApplyReduceDDObject<T> extends Msg implements Serializable {
    Reduction<T> reduction;
    boolean allowIncompleteResults;

    public MsgApplyReduceDDObject(String ddui, String requestId, Reduction<T> reduction,
                                  boolean allowIncompleteResults) {
        super(ddui, requestId);
        this.reduction = reduction;
        this.allowIncompleteResults = allowIncompleteResults;
    }

    public Reduction<T> getReduction() {
        return reduction;
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
        return new MsgApplyReduceDDObjectReply<T>(getDDUI(), getRequestId(), null, false, false, failureReason);
    }

    public MsgApplyReduceDDObjectReply<T> getSuccessReplyMessage(T result) {
        return new MsgApplyReduceDDObjectReply<>(getDDUI(), getRequestId(), result, false, true, null);
    }
}
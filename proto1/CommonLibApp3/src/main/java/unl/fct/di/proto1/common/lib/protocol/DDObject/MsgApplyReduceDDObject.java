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

    public MsgApplyReduceDDObject(String ddui, String requestId, Reduction<T> reduction) {
        super(ddui, requestId);
        this.reduction = reduction;
    }

    public Reduction<T> getReduction() {
        return reduction;
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyReduceDDObjectReply<T>(getDDUI(), getRequestId(), null, false, failureReason);
    }

    public Msg getSuccessReplyMessage(T result) {
        return new MsgApplyReduceDDObjectReply<>(getDDUI(), getRequestId(), result, true, null);
    }
}
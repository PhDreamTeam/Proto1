package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class MsgGetCountDDObject extends Msg implements Serializable {
    boolean allowIncompleteResults;

    public MsgGetCountDDObject(String DDUI, String requestId, boolean allowIncompleteResults) {
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
        return new MsgGetCountDDObjectReply(getDDUI(), getRequestId(), 0, false, false, failureReason);
    }

    public MsgGetCountDDObjectReply getSuccessReplyMessage(int count) {
        return new MsgGetCountDDObjectReply(getDDUI(), getRequestId(), count, false, true, null);
    }
}
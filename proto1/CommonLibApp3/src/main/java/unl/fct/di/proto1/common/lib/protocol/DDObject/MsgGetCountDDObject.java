package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class MsgGetCountDDObject extends Msg implements Serializable {

    public MsgGetCountDDObject(String DDUI, String requestId) {
        super(DDUI, requestId);
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgGetCountDDObjectReply(getDDUI(), getRequestId(), 0, false, failureReason);
    }

    public Msg getSuccessReplyMessage(int count) {
        return new MsgGetCountDDObjectReply(getDDUI(), getRequestId(), count, true, null);
    }
}
package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;

public class MsgOpenDDObject extends Msg implements Serializable {

    public MsgOpenDDObject(String DDUI, String requestId) {
        super(DDUI, requestId);
    }


    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgOpenDDObjectReply(getDDUI(), getRequestId(), 0, false, failureReason);
    }
}

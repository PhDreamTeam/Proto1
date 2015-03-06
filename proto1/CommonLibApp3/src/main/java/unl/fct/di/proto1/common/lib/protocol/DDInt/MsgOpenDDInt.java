package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;

public class MsgOpenDDInt extends Msg implements Serializable {

    public MsgOpenDDInt(String DDUI, String requestId) {
        super(DDUI, requestId);
    }


    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgOpenDDIntReply(getDDUI(), getRequestId(), 0, false, failureReason);
    }
}

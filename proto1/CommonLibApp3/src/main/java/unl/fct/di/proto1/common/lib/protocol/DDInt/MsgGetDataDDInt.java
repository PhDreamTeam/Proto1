package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;


public class MsgGetDataDDInt extends Msg implements Serializable{

    public MsgGetDataDDInt(String DDUI, String requestId) {
        super(DDUI, requestId);
    }


    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgGetDataDDIntReply(getDDUI(), getRequestId(), null, false, failureReason);
    }
}

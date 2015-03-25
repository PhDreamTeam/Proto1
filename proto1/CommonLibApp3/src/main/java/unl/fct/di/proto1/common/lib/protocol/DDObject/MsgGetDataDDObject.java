package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;


public class MsgGetDataDDObject<T> extends Msg implements Serializable{

    public MsgGetDataDDObject(String DDUI, String requestId) {
        super(DDUI, requestId);
    }


    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgGetDataDDObjectReply<>(getDDUI(), getRequestId(), null, false, failureReason);
    }

    public MsgGetDataDDObjectReply<T> getSuccessReplyMessage(T[] data) {
        return new MsgGetDataDDObjectReply<>(getDDUI(), getRequestId(), data, true, null);
    }
}

package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;
import java.util.Arrays;

public class MsgCreateDDObject<T> extends Msg implements Serializable {
    T[] data;

    public MsgCreateDDObject(String DDUI, String requestId, T[] data) {
        super(DDUI, requestId);
        this.data = data;
    }

    public T[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgCreateDDObjectReply(getDDUI(), getRequestId(), false, failureReason);
    }

    public Msg getSuccessReplyMessage() {
        return new MsgCreateDDObjectReply(getDDUI(), getRequestId(), true, null);
    }
}

package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;
import java.util.Arrays;

public class MsgCreateDDObject extends Msg implements Serializable {
    Object[] data;

    public MsgCreateDDObject(String DDUI, String requestId, Object[] data) {
        super(DDUI, requestId);
        this.data = data;
    }

    public Object[] getData() {
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
}

package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.Msg;

import java.io.Serializable;
import java.util.Arrays;

public class MsgCreateDDInt extends Msg implements Serializable {
    int[] data;

    public MsgCreateDDInt(String DDUI, String requestId, int[] data) {
        super(DDUI, requestId);
        this.data = data;
    }

    public int[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgCreateDDIntReply(getDDUI(), getRequestId(), false, failureReason);
    }
}

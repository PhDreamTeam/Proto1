package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;


public class MsgRegisterReply extends MsgReply implements Serializable {

    public MsgRegisterReply(String requestId, boolean success, String failureReason) {
        super(null, requestId, success, failureReason);
    }
}

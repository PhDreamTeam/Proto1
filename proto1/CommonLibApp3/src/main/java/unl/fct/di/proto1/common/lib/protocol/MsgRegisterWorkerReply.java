package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;


public class MsgRegisterWorkerReply extends MsgRegisterReply implements Serializable {

    public MsgRegisterWorkerReply(String requestId, boolean success, String failureReason) {
        super(requestId, success, failureReason);
    }
}

package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;


public class MsgCreateDDObjectReply extends MsgReply implements Serializable {

    public MsgCreateDDObjectReply(String DDUI, String requestId,
                                  boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
    }
}

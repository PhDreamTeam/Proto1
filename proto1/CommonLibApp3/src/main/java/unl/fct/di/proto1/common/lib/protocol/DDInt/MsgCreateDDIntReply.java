package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;


public class MsgCreateDDIntReply extends MsgReply implements Serializable {

    public MsgCreateDDIntReply(String DDUI, String requestId,
                                  boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
    }
}

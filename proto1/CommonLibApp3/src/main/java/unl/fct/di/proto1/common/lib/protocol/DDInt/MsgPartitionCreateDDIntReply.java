package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;

public class MsgPartitionCreateDDIntReply extends MsgPartitionReply implements Serializable {

    public MsgPartitionCreateDDIntReply(String DDUI, String requestId, int partId,
                                           boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
    }
}

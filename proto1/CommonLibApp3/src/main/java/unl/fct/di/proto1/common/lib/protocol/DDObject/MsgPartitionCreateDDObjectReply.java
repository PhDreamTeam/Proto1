package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;

public class MsgPartitionCreateDDObjectReply extends MsgPartitionReply implements Serializable {

    public MsgPartitionCreateDDObjectReply(String DDUI, String requestId, int partId,
                                           boolean success, String failureReason) {
      super(DDUI, requestId, partId, success, failureReason);
    }
}

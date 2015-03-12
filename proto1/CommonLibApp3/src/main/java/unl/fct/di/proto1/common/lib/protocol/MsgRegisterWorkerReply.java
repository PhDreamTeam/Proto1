package unl.fct.di.proto1.common.lib.protocol;

import unl.fct.di.proto1.common.lib.ActorNode;

import java.io.Serializable;


public class MsgRegisterWorkerReply extends MsgRegisterReply implements Serializable {
    ActorNode an;
    int[] partitionIds;

    public MsgRegisterWorkerReply(String requestId, int[] partitionIds, ActorNode an, boolean success, String failureReason) {
        super(requestId, success, failureReason);
        this.partitionIds = partitionIds;
        this.an = an;
    }

    public ActorNode getAn() {
        return an;
    }

    public int[] getPartitionIds() {
        return partitionIds;
    }
}

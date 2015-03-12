package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;


public class MsgRegisterWorkerReply extends MsgRegisterReply implements Serializable {

    int[] partitionIds;

    public MsgRegisterWorkerReply(String requestId, int[] partitionIds, boolean success, String failureReason) {
        super(requestId, success, failureReason);
        this.partitionIds = partitionIds;
    }

    public int[] getPartitionIds() {
        return partitionIds;
    }
}

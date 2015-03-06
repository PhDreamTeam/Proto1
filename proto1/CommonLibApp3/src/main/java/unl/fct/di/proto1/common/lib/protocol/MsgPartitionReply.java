package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;


public class MsgPartitionReply extends MsgReply implements Serializable {
    int partId;


    public MsgPartitionReply(String DDUI, String requestId, int partId,
                             boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.partId = partId;
    }

    public int getPartId() {
        return partId;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return ", partId: " + getPartId();
    }
}

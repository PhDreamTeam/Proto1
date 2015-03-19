package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class MsgPartitionGetCountDDObjectReply extends MsgPartitionReply implements Serializable {
    int count;

    public MsgPartitionGetCountDDObjectReply(String DDUI, String requestId, int partId,
                                                int count,
                                                boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", count: " + getCount();
    }


}

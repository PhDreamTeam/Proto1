package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class MsgGetCountDDObjectReply extends MsgReply implements Serializable {
    int count;

    public MsgGetCountDDObjectReply(String DDUI, String requestId, int count,
                                   boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", count: " +  count;
    }
}

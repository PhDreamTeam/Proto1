package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class MsgGetCountDDObjectReply extends MsgReply implements Serializable {
    int count;
    boolean hasIncompleteResults;

    public MsgGetCountDDObjectReply(String DDUI, String requestId, int count,
                                    boolean hasIncompleteResults, boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.count = count;
        this.hasIncompleteResults = hasIncompleteResults;
    }

    public boolean hasIncompleteResults() {
        return hasIncompleteResults;
    }

    public int getCount() {
        return count;
    }

    public void setIncompleteResults(String failureReason) {
        hasIncompleteResults = true;
        setFailureReason(failureReason);
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", count: " +  count +
                (hasIncompleteResults ? " has incomplete results: " + getFailureReason() : "");
    }
}

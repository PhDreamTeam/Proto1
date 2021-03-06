package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;
import java.util.Arrays;



public class MsgGetDataDDObjectReply<T> extends MsgReply implements Serializable {
    T[] data;
    boolean hasIncompleteResults;

    public MsgGetDataDDObjectReply(String DDUI, String requestId, T[] data,
                                   boolean hasIncompleteResults, boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.data = data;
        this.hasIncompleteResults = hasIncompleteResults;
    }

    public boolean hasIncompleteResults() {
        return hasIncompleteResults;
    }

    public void setIncompleteResults(String failureReason) {
        hasIncompleteResults = true;
        setFailureReason(failureReason);
    }

    public T[] getData() {
        return data;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", " +  Arrays.toString(data) +
                (hasIncompleteResults ? " has incomplete results: " + getFailureReason() : "");
    }
}

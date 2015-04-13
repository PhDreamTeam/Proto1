package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

public class MsgApplyReduceDDObjectReply<T> extends MsgReply implements Serializable {
    T result;
    boolean hasIncompleteResults;

    public MsgApplyReduceDDObjectReply(String DDUI, String requestId, T result, boolean hasIncompleteResults,
                                        boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.result = result;
        this.hasIncompleteResults = hasIncompleteResults;
    }

    public T getResult() {
        return result;
    }

    public boolean hasIncompleteResults() {
        return hasIncompleteResults;
    }

    public void setIncompleteResults(String failureReason) {
        hasIncompleteResults = true;
        setFailureReason(failureReason);
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() +  ", result: " + getResult()+
                (hasIncompleteResults ? " has incomplete results: " + getFailureReason() : "");
    }
}

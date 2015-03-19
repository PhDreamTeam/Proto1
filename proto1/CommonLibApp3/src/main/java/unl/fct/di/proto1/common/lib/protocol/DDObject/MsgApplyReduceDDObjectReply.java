package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

public class MsgApplyReduceDDObjectReply<T> extends MsgReply implements Serializable {
    T result;

    public MsgApplyReduceDDObjectReply(String DDUI, String requestId, T result,
                                       boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() +  ", result: " + getResult();
    }
}

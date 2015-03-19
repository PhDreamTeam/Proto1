package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;

/**
 * Created by At DR on 19-03-2015.
 *
 */
public class MsgPartitionApplyReduceDDObjectReply extends MsgPartitionReply implements Serializable {
    Object result;

    public MsgPartitionApplyReduceDDObjectReply(String DDUI, String requestId, int partId,
                                                  Object result,
                                                  boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", result: " + getResult();
    }


}
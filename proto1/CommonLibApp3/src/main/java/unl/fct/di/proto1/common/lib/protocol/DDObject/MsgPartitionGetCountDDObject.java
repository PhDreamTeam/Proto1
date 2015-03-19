package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;


public class MsgPartitionGetCountDDObject<T> extends MsgPartitionRequest implements Serializable {

    public MsgPartitionGetCountDDObject(String DDUI, String requestId, int partId) {
        super(DDUI, requestId, partId);
    }

    @Override
    public MsgPartitionGetCountDDObjectReply getFailureReplyMessage(String failureReason) {
        return new MsgPartitionGetCountDDObjectReply(getDDUI(), getRequestId(), getPartId(), 0, false, failureReason);
    }

    public MsgPartitionGetCountDDObjectReply getSuccessReplyMessage(int count) {
        return  new MsgPartitionGetCountDDObjectReply(getDDUI(), getRequestId(), getPartId(), count, true, null);
    }
}

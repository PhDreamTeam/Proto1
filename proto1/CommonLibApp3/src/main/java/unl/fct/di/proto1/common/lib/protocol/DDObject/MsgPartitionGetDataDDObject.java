package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;

public class MsgPartitionGetDataDDObject<T> extends MsgPartitionRequest implements Serializable {

    public MsgPartitionGetDataDDObject(String DDUI, String requestId, int partId) {
        super(DDUI, requestId, partId);
    }

    @Override
    public MsgPartitionGetDataDDObjectReply<T> getFailureReplyMessage(String failureReason) {
        return new MsgPartitionGetDataDDObjectReply<>(getDDUI(), getRequestId(), getPartId(), null, false, failureReason);
    }

    public MsgPartitionGetDataDDObjectReply<T>  getSuccessReplyMessage(T[] data) {
        return new MsgPartitionGetDataDDObjectReply<>(getDDUI(), getRequestId(), getPartId(), data, true, null);
    }
}

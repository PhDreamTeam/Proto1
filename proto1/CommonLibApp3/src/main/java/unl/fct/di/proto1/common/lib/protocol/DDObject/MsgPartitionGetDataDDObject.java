package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;

public class MsgPartitionGetDataDDObject extends MsgPartitionRequest implements Serializable {

    public MsgPartitionGetDataDDObject(String DDUI, String requestId, int partId) {
        super(DDUI, requestId, partId);
    }

    @Override
    public MsgPartitionGetDataDDObjectReply getFailureReplyMessage(String failureReason) {
        return new MsgPartitionGetDataDDObjectReply(getDDUI(), getRequestId(), getPartId(), null, false, failureReason);
    }
}

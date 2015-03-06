package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;

public class MsgPartitionGetDataDDInt extends MsgPartitionRequest implements Serializable {

    public MsgPartitionGetDataDDInt(String DDUI, String requestId, int partId) {
        super(DDUI, requestId, partId);
    }

    @Override
    public MsgPartitionGetDataDDIntReply getFailureReplyMessage(String failureReason) {
        return new MsgPartitionGetDataDDIntReply(getDDUI(), getRequestId(), getPartId(), null, false, failureReason);
    }
}

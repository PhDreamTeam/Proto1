package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;
import java.util.Arrays;

public class MsgPartitionGetDataDDObjectReply<T> extends MsgPartitionReply implements Serializable {
    T[] data;

    public MsgPartitionGetDataDDObjectReply(String DDUI, String requestId, int partId, T[] data,
                                            boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.data = data;
    }

    public T[] getData() {
        return data;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + " " + Arrays.toString(data);
    }
}

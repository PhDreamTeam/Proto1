package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;
import java.util.Arrays;

public class MsgPartitionGetDataDDIntReply extends MsgPartitionReply implements Serializable {
    int[] data;


    public MsgPartitionGetDataDDIntReply(String DDUI, String requestId, int partId, int[] data,
                                            boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.data = data;
    }

    public int[] getData() {
        return data;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + " " + Arrays.toString(data);
    }
}

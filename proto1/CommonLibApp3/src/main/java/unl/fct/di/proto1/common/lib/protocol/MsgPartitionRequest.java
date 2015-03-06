package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;


public class MsgPartitionRequest extends Msg implements Serializable {
    int partId;


    public MsgPartitionRequest(String DDUI, String requestId, int partId) {
        super(DDUI, requestId);
        this.partId = partId;
    }

    public int getPartId() {
        return partId;
    }


    @Override
    public String toString() {
        return super.toString() + ", partId: " + getPartId();
    }
}

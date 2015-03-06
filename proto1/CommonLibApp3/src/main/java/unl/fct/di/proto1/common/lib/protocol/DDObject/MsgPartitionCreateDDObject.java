package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;
import java.util.Arrays;

public class MsgPartitionCreateDDObject extends MsgPartitionRequest implements Serializable {
    Object[] data;

    public MsgPartitionCreateDDObject(String DDUI, String requestId, int partId, Object[] data) {
        super(DDUI, requestId, partId);
        this.data = data;
    }

    public Object[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }
}

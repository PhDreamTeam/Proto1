package unl.fct.di.proto1.common.lib.protocol.DDObject;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;
import java.util.Arrays;

public class MsgPartitionCreateDDObject<T> extends MsgPartitionRequest implements Serializable {
    T[] data;

    public MsgPartitionCreateDDObject(String DDUI, String requestId, int partId, T[] data) {
        super(DDUI, requestId, partId);
        this.data = data;
    }

    public T[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }
}

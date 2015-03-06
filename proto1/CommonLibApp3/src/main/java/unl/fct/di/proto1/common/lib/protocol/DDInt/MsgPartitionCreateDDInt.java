package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;
import java.util.Arrays;

public class MsgPartitionCreateDDInt extends MsgPartitionRequest implements Serializable {
    int[] data;

    public MsgPartitionCreateDDInt(String DDUI, String requestId, int partId, int[] data) {
        super(DDUI, requestId, partId);
        this.data = data;
    }

    public int[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }
}

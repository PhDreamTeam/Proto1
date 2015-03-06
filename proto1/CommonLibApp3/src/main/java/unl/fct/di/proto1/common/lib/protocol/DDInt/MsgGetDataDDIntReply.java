package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;
import java.util.Arrays;


public class MsgGetDataDDIntReply  extends MsgReply implements Serializable {

    int[] data;
    public MsgGetDataDDIntReply(String DDUI, String requestId, int[] data,
                                   boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.data = data;
    }


    public int[] getData() {
        return data;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", " +  Arrays.toString(data);
    }
}

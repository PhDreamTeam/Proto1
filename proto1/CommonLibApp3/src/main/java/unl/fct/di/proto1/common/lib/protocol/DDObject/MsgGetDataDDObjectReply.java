package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;
import java.util.Arrays;



public class MsgGetDataDDObjectReply extends MsgReply implements Serializable {
    Object[] data;

    public MsgGetDataDDObjectReply(String DDUI, String requestId, Object[] data,
                                   boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.data = data;
    }


    public Object[] getData() {
        return data;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", " +  Arrays.toString(data);
    }
}

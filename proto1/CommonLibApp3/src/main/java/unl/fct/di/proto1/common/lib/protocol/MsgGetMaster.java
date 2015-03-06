package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;


public class MsgGetMaster extends Msg implements Serializable {

    public MsgGetMaster(String requestId) {
        super(null, requestId);
    }
}

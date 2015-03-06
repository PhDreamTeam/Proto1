package unl.fct.di.proto1.common.lib.protocol;


import java.io.Serializable;

public class Msg implements Serializable {
    private String DDUI;
    private String requestId;

    public Msg(String DDUI, String requestId) {
        this.DDUI = DDUI;
        this.requestId = requestId;
    }


    public String getDDUI() {
        return DDUI;
    }

    public String getRequestId() {
        return requestId;
    }

    public String toString() {
        return getClass().getSimpleName()+ " " + getDDUI() + ", reqId: " + getRequestId();
    }




    public Msg getFailureReplyMessage(String failureReason) {
        return null;
    }
}

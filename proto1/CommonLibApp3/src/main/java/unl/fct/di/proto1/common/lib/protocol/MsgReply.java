package unl.fct.di.proto1.common.lib.protocol;

import java.io.Serializable;

// TODO erase success field and let the success be implicitly determined by failure reason equal to null

public class MsgReply extends Msg implements Serializable {
    boolean success;
    String failureReason;

    public MsgReply(String DDUI, String requestId, boolean success, String failureReason) {
        super(DDUI, requestId);
        this.success = success;
        this.failureReason = failureReason;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public String toString() {
        return super.toString() + getIntermediateInfo() +
                (isSuccess() ? ", success" : ", failure: " + getFailureReason());
    }

    // to be called by toString and redefined by subclasses
    public String getIntermediateInfo() {
        return "";
    }
}

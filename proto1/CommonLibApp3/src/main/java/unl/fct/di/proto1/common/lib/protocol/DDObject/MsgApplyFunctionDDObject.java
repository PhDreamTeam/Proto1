package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class MsgApplyFunctionDDObject extends Msg implements Serializable {
    String newDDUI;
    Function<Object, Object> action;

    public MsgApplyFunctionDDObject(String DDUI, String requestId, String newDDUI,
                                    Function<Object, Object> action) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.action = action;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Function<Object, Object> getAction() {
        return action;
    }


    @Override
    public String toString() {
        return super.toString() + ", newDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFunctionDDObjectReply(getDDUI(), getRequestId(), null, false, failureReason);
    }
}
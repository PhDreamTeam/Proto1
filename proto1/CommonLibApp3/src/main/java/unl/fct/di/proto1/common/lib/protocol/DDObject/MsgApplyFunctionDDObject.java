package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class MsgApplyFunctionDDObject<T, R> extends Msg implements Serializable {
    String newDDUI;
    Function<T, R> action;
    R[] arrayRType;

    public MsgApplyFunctionDDObject(String DDUI, String requestId, String newDDUI,
                                    Function<T, R> action, R[] arrayRType) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.action = action;
        this.arrayRType = arrayRType;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Function<T, R> getAction() {
        return action;
    }

    public R[] getArrayRType() {
        return arrayRType;
    }

    @Override
    public String toString() {
        return super.toString() + ", srcDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFunctionDDObjectReply(getDDUI(), getRequestId(), newDDUI, false, failureReason);
    }

    public Msg getSuccessReplyMessage() {
        return new MsgApplyFunctionDDObjectReply(getDDUI(), getRequestId(), newDDUI, true, null);
    }

}
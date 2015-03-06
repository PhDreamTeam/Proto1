package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class MsgApplyFunctionDDInt extends Msg implements Serializable {
    String newDDUI;
    Function<Integer, Integer> action;

    public MsgApplyFunctionDDInt(String DDUI, String requestId, String newDDUI,
                                 Function<Integer, Integer> action) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.action = action;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Function<Integer, Integer> getAction() {
        return action;
    }


    @Override
    public String toString() {
        return super.toString() + ", newDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFunctionDDIntReply(getDDUI(), getRequestId(), null, false, failureReason);
    }
}
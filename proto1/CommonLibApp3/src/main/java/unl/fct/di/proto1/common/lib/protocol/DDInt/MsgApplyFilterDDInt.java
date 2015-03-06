package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class MsgApplyFilterDDInt extends Msg implements Serializable {
    String newDDUI;
    Predicate<Integer> filter;

    public MsgApplyFilterDDInt(String DDUI, String requestId, String newDDUI,
                               Predicate<Integer> filter) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.filter = filter;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Predicate<Integer> getFilter() {
        return filter;
    }



    @Override
    public String toString() {
        return super.toString() + " newDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFilterDDIntReply(getDDUI(), getRequestId(), null, 0, false, failureReason);
    }
}
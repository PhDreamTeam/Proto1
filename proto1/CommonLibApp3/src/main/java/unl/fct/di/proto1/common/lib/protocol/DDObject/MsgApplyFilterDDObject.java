package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class MsgApplyFilterDDObject extends Msg implements Serializable {
    String newDDUI;
    Predicate<Object> filter;

    public MsgApplyFilterDDObject(String DDUI, String requestId, String newDDUI,
                                  Predicate<Object> filter) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.filter = filter;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Predicate<Object> getFilter() {
        return filter;
    }



    @Override
    public String toString() {
        return super.toString() + " newDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFilterDDObjectReply(getDDUI(), getRequestId(), null, 0, false, failureReason);
    }
}
package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class MsgApplyFilterDDObject<T> extends Msg implements Serializable {
    String newDDUI;
    Predicate<T> filter;

    public MsgApplyFilterDDObject(String DDUI, String requestId, String newDDUI,
                                  Predicate<T> filter) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.filter = filter;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Predicate<T> getFilter() {
        return filter;
    }



    @Override
    public String toString() {
        return super.toString() + " srcDDUI: " + getNewDDUI();
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyFilterDDObjectReply(getDDUI(), getRequestId(), newDDUI, 0, false, failureReason);
    }

    public Msg getSuccessReplyMessage(int nElems) {
        return new MsgApplyFilterDDObjectReply(getDDUI(), getRequestId(), newDDUI, nElems, true, null);
    }
}
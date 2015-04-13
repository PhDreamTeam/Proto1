package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;

import java.io.Serializable;


public class MsgApplyMapDDObject<T, R> extends Msg implements Serializable {
    String newDDUI;
    MapFunction<T, R> mapFunction;
    R[] arrayRType;
    boolean allowIncompleteResults;

    public MsgApplyMapDDObject(String DDUI, String requestId, String newDDUI,
                               MapFunction<T, R> mapFunction, R[] arrayRType, boolean allowIncompleteResults) {
        super(DDUI, requestId);
        this.newDDUI = newDDUI;
        this.mapFunction = mapFunction;
        this.arrayRType = arrayRType;
        this.allowIncompleteResults = allowIncompleteResults;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public MapFunction<T, R> getMapFunction() {
        return mapFunction;
    }

    public R[] getArrayRType() {
        return arrayRType;
    }

    public boolean allowIncompleteResults() {
        return allowIncompleteResults;
    }

    @Override
    public String toString() {
        return super.toString() + ", srcDDUI: " + getNewDDUI() +
                (allowIncompleteResults ? " allows incomplete results" : "");
    }

    @Override
    public Msg getFailureReplyMessage(String failureReason) {
        return new MsgApplyMapDDObjectReply(getDDUI(), getRequestId(), newDDUI, 0,false, false, failureReason);
    }

    public MsgApplyMapDDObjectReply getSuccessReplyMessage(int nDataElems) {
        return new MsgApplyMapDDObjectReply(getDDUI(), getRequestId(), newDDUI, nDataElems, false, true, null);
    }

}
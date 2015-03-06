package unl.fct.di.proto1.common.lib.protocol.DDInt;


import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;

public class MsgApplyFunctionDDIntReply extends MsgReply implements Serializable {
    String newDDUI;


    public MsgApplyFunctionDDIntReply(String DDUI, String requestId, String newDDUI,
                                      boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.newDDUI = newDDUI;
    }


    public String getNewDDUI() {
        return newDDUI;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() +  ", newDDUI: " + getNewDDUI();
    }
}

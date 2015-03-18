package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;


public class MsgPartitionApplyFunctionDDObjectReply extends MsgPartitionReply implements Serializable {
    String newDDUI;

    public MsgPartitionApplyFunctionDDObjectReply(String DDUI, String requestId, int partId,
                                                  String newDDUI, boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.newDDUI = newDDUI;
    }

    public String getNewDDUI() {
        return newDDUI;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", srcDDUI: " + getNewDDUI();
    }
}

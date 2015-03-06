package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;


public class MsgPartitionApplyFilterDDIntReply extends MsgPartitionReply implements Serializable {
    String newDDUI;
    int nElems;

    public MsgPartitionApplyFilterDDIntReply(String DDUI, String requestId, int partId,
                                             String newDDUI, int nElems, boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.newDDUI = newDDUI;
        this.nElems = nElems;
    }

    public String getNewDDUI() {
        return newDDUI;
    }

    public int getNElems() {
        return nElems;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", newDDUI: " + getNewDDUI() + ", nElems: " + getNElems();
    }
}

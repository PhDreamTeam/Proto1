package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;


public class MsgPartitionApplyMergeDDObjectReply extends MsgPartitionReply implements Serializable {
    String srcDDUI;
    int srcPartId, nElems;

    public MsgPartitionApplyMergeDDObjectReply(String DDUI, String requestId, int partId,
                                               String srcDDUI, int srcPartId, int nElems, boolean success, String failureReason) {
        super(DDUI, requestId, partId, success, failureReason);
        this.srcDDUI = srcDDUI;
        this.srcPartId = srcPartId;
        this.nElems = nElems;
    }

    public String getSrcDDUI() {
        return srcDDUI;
    }

    public int getSrcPartId() {
        return srcPartId;
    }

    public int getNElems() {
        return nElems;
    }

    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", srcDDUI: " + getSrcDDUI() + ", srcPartId: " + getSrcPartId()
                + ", nElems: " + getNElems();
    }
}

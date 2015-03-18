package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.io.Serializable;


public class MsgPartitionApplyMergeDDObject extends MsgPartitionRequest implements Serializable {
    String srcDDUI;
    int srcPartId;


    public MsgPartitionApplyMergeDDObject(String newDDUI, String requestId, int newPartId,
                                          String srcDDUI, int srcPartId ) {
        super(newDDUI, requestId, newPartId);
        this.srcDDUI = srcDDUI;
        this.srcPartId = srcPartId;
    }


    public String getSrcDDUI() {
        return srcDDUI;
    }

    public int getSrcPartId() {
        return srcPartId;
    }

    @Override
    public String toString() {
        return super.toString() +  ", srcDDUI: " + getSrcDDUI() + ", srcPartId: " + getSrcPartId();
    }
}

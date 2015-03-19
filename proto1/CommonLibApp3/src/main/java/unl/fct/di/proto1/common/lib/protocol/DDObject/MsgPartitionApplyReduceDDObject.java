package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Reduction;

import java.io.Serializable;


public class MsgPartitionApplyReduceDDObject<T> extends MsgPartitionRequest implements Serializable {
    Reduction<T> reduction;

    public MsgPartitionApplyReduceDDObject(String DDUI, String requestId, int partId, Reduction<T> reduction) {
        super(DDUI, requestId, partId);
        this.reduction = reduction;
    }

    public Reduction<T> getReduction() {
        return reduction;
    }

}

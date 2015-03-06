package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class MsgPartitionApplyFunctionDDObject extends MsgPartitionRequest implements Serializable {
    Function<Object, Object> action;
    String newDDUI;

    public MsgPartitionApplyFunctionDDObject(String DDUI, String requestId, int partId, String newDDUI,
                                             Function<Object, Object> action) {
        super(DDUI, requestId, partId);
        this.newDDUI = newDDUI;
        this.action = action;
    }

    public String getNewDDUI() {
        return newDDUI;
    }

    public Function<Object, Object> getAction() {
        return action;
    }


    @Override
    public String toString() {
        return super.toString() + ", newDDUI: " + getNewDDUI();
    }
}

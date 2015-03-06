package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class MsgPartitionApplyFunctionDDInt extends MsgPartitionRequest implements Serializable {
    Function<Integer, Integer> action;
    String newDDUI;

    public MsgPartitionApplyFunctionDDInt(String DDUI, String requestId, int partId, String newDDUI,
                                          Function<Integer, Integer> action) {
        super(DDUI, requestId, partId);
        this.newDDUI = newDDUI;
        this.action = action;
    }

    public String getNewDDUI() {
        return newDDUI;
    }

    public Function<Integer, Integer> getAction() {
        return action;
    }


    @Override
    public String toString() {
        return super.toString() + ", newDDUI: " + getNewDDUI();
    }
}

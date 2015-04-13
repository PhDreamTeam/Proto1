package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;
import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;

import java.io.Serializable;


public class MsgPartitionApplyMapDDObject<T, R> extends MsgPartitionRequest implements Serializable {
    MapFunction<T, R> action;
    String newDDUI;
    R[] arrayRType;

    public MsgPartitionApplyMapDDObject(String DDUI, String requestId, int partId, String newDDUI,
                                        MapFunction<T, R> action, R[] arrayRType) {
        super(DDUI, requestId, partId);
        this.newDDUI = newDDUI;
        this.action = action;
        this.arrayRType = arrayRType;
    }

    public String getNewDDUI() {
        return newDDUI;
    }

    public MapFunction<T, R> getAction() {
        return action;
    }

    public R[] getArrayRType() {
        return arrayRType;
    }

    @Override
    public String toString() {
        return super.toString() + ", srcDDUI: " + getNewDDUI();
    }
}

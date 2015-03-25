package unl.fct.di.proto1.common.lib.protocol.DDObject;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class MsgPartitionApplyFilterDDObject<T> extends MsgPartitionRequest implements Serializable {
    Predicate<T> filter;
    String newDDUI;


    public MsgPartitionApplyFilterDDObject(String DDUI, String requestId, int partId, String newDDUI,
                                           Predicate<T> filter) {
        super(DDUI, requestId, partId);
        this.newDDUI = newDDUI;
        this.filter = filter;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Predicate<T> getFilter() {
        return filter;
    }


    @Override
    public String toString() {
        return super.toString() +  ", srcDDUI: " + getNewDDUI();
    }
}

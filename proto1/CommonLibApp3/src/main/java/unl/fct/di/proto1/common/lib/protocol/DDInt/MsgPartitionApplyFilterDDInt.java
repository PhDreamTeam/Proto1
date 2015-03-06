package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class MsgPartitionApplyFilterDDInt extends MsgPartitionRequest implements Serializable {
    Predicate<Integer> filter;
    String newDDUI;


    public MsgPartitionApplyFilterDDInt(String DDUI, String requestId, int partId, String newDDUI,
                                        Predicate<Integer> filter) {
        super(DDUI, requestId, partId);
        this.newDDUI = newDDUI;
        this.filter = filter;
    }


    public String getNewDDUI() {
        return newDDUI;
    }

    public Predicate<Integer> getFilter() {
        return filter;
    }


    @Override
    public String toString() {
        return super.toString() +  ", newDDUI: " + getNewDDUI();
    }
}

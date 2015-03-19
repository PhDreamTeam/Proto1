package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Reduction;


public class DDObjectReductionAdd_1 implements Reduction<Integer> {
    public Integer reduce(Integer i1, Integer i2) {
        return i1 + i2;
    }
}
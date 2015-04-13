package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;

import java.io.Serializable;


public class DDIntMapFunctionAdd_1 implements MapFunction<Integer, Integer>, Serializable {
    public Integer apply(Integer value) {
        return value + 10;
    }
}
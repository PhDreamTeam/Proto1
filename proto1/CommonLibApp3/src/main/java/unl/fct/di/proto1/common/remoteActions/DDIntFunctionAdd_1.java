package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class DDIntFunctionAdd_1 implements Function<Integer, Integer>, Serializable {
    public Integer apply(Integer value) {
        return value + 10;
    }
}
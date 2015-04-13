package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;

import java.io.Serializable;


public class DDObjectMapFunctionAddLength_1 implements MapFunction<String, String>, Serializable {
    public String apply(String value) {
        return value.toUpperCase() + " (" + value.length() + ")";
    }
}
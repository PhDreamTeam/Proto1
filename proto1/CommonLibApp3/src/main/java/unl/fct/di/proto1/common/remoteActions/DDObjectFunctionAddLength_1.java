package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class DDObjectFunctionAddLength_1 implements Function<String, String>, Serializable {
    public String apply(String value) {
        return value.toUpperCase() + " (" + value.length() + ")";
    }
}
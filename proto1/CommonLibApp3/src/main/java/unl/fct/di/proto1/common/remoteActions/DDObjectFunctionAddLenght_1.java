package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class DDObjectFunctionAddLenght_1 implements Function<Object, Object>, Serializable {
    public Object apply(Object value) {
        String val = (String) value;
        return val.toUpperCase() + " (" + val.length() + ")";
    }
}
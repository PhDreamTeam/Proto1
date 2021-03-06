package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class DDObjectFilterContainsString_1 implements Predicate<String>, Serializable {
    String val;

    public DDObjectFilterContainsString_1(String val) {
        this.val = val;
    }

    @Override
    public boolean test(String value) {
        return value.contains(val);
    }
}
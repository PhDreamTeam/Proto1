package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class DDIntFilterGreaterThen_1 implements Predicate<Integer>, Serializable {
    int lowerLimit;

    public DDIntFilterGreaterThen_1(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @Override
    public boolean test(Integer value) {
        return lowerLimit < value;
    }
}
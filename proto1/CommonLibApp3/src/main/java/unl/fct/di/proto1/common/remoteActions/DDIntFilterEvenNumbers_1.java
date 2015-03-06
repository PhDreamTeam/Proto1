package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;


public class DDIntFilterEvenNumbers_1 implements Predicate<Integer>, Serializable {

    @Override
    public boolean test(Integer value) {
        return value % 2 == 0;
    }
}
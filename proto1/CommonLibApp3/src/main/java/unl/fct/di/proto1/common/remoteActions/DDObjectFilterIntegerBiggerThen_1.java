package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;

/**
 * Created by AT DR on 17/03/2015.
 *
 */

public class DDObjectFilterIntegerBiggerThen_1 implements Predicate<Integer>, Serializable {
    int limit;

    public DDObjectFilterIntegerBiggerThen_1(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean test(Integer value) {
        return value > limit;
    }

}
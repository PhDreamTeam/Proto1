package unl.fct.di.proto1.common.remoteActions;

import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by AT DR on 17/03/2015.
 *
 */

public class DDObjectFilterIntegerBiggerThen_1 implements Predicate<Object>, Serializable {
    int limit;

    public DDObjectFilterIntegerBiggerThen_1(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean test(Object value) {
        return (Integer)value > limit;
    }

}
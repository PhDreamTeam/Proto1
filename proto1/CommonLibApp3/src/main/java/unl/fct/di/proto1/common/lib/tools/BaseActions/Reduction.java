package unl.fct.di.proto1.common.lib.tools.BaseActions;

import java.io.Serializable;

public interface Reduction<T>  extends Serializable {
    // reduce must be an associative and commutative function
    T reduce(T o1, T o2);
}

package unl.fct.di.proto1.common.lib.tools.BaseActions;


import java.io.Serializable;

public interface Predicate<T>  extends Serializable {
    boolean test(T t);
}

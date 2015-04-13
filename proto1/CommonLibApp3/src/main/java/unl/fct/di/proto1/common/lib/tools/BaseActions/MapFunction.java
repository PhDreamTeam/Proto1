package unl.fct.di.proto1.common.lib.tools.BaseActions;

import java.io.Serializable;

public interface MapFunction<T, R>  extends Serializable {
    R apply(T t);
}

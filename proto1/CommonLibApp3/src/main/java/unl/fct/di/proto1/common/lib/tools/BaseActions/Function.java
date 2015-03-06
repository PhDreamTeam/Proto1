package unl.fct.di.proto1.common.lib.tools.BaseActions;

import java.io.Serializable;

public interface Function<T, R>  extends Serializable {
    R apply(T t);
}

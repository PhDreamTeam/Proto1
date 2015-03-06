package unl.fct.di.proto1.common.lib.tools.BaseActions;


import java.io.Serializable;

public interface Consumer<T> extends Serializable {
    void accept(T t);
}

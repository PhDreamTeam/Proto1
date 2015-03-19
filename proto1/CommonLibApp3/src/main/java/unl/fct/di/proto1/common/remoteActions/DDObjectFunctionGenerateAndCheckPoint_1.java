package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

/**
 * Created by AT DR on 19-03-2015.
 *
 */
public class DDObjectFunctionGenerateAndCheckPoint_1 implements Function<Object, Object> {
    public Object apply(Object value) {
        double x = Math.random() * 2 - 1;
        double y = Math.random() * 2 - 1;
        return (x * x + y * y < 1) ? 1 : 0;
    }
}


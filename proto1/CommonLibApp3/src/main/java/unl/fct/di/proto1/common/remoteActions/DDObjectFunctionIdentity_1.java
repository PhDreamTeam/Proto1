package unl.fct.di.proto1.common.remoteActions;

import pt.unl.fct.di.proto1.services.photos.Photo;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class DDObjectFunctionIdentity_1 implements Function<Photo, Integer>, Serializable {
    public Integer apply(Photo value) {
        return 1;
    }
}
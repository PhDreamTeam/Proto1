package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;

import java.io.Serializable;


public class DDObjectMapFunctionIdentity_1 implements MapFunction<IPhotoRemote, Integer>, Serializable {
    public Integer apply(IPhotoRemote value) {
        return 1;
    }
}
package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.Serializable;


public class DDObjectFunctionIdentity_1 implements Function<IPhotoRemote, Integer>, Serializable {
    public Integer apply(IPhotoRemote value) {
        return 1;
    }
}
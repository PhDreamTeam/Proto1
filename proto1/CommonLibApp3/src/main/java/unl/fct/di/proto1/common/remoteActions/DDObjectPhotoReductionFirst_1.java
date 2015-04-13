package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Reduction;


public class DDObjectPhotoReductionFirst_1 implements Reduction<IPhotoRemote> {
    public IPhotoRemote reduce(IPhotoRemote i1, IPhotoRemote i2) {
        return i1;
    }
}
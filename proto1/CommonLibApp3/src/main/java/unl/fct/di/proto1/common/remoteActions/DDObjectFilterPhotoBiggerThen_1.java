package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;

/**
 * Created by AT DR on 17/03/2015.
 *
 */
public class DDObjectFilterPhotoBiggerThen_1 implements Predicate<IPhotoRemote>, Serializable {
    int limit;

    public DDObjectFilterPhotoBiggerThen_1(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean test(IPhotoRemote value) {
        try {
            return value.getPhotoInBytes().length > limit;
        } catch (Exception e) {
            return false;
        }
    }

}
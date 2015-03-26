package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.Serializable;

/**
 * Created by AT DR on 17/03/2015.
 *
 */
public class DDObjectFilterPhotoBetweenThen_1 implements Predicate<IPhotoRemote>, Serializable {
    int lowerLimit, upperLimit;

    public DDObjectFilterPhotoBetweenThen_1(int lowerLimit, int upperLimit) {

        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    @Override
    public boolean test(IPhotoRemote value) {
        try {
            int length = value.getPhotoInBytes().length;
            return length >= lowerLimit && length <= upperLimit ;
        } catch (Exception e) {
            return false;
        }
    }

}
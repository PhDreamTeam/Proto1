package unl.fct.di.proto1.common.remoteActions;

import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by AT DR on 17/03/2015.
 *
 */
public class DDObjectFilterPhotoBetweenThen_1 implements Predicate<Object>, Serializable {
    int lowerLimit, upperLimit;

    public DDObjectFilterPhotoBetweenThen_1(int lowerLimit, int upperLimit) {

        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    @Override
    public boolean test(Object value) {
        PhotoWorker pw = (PhotoWorker) value;
        try {
            int length = pw.getPhotoInBytes().length;
            return length >= lowerLimit && length <= upperLimit ;
        } catch (IOException e) {
            return false;
        }
    }

}
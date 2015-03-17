package unl.fct.di.proto1.common.remoteActions;

import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by AT DR on 17/03/2015.
 *
 */
// TODO change predicate type Object to PhotoWorker
public class DDObjectFilterPhotoBiggerThen_1 implements Predicate<Object>, Serializable {
    int limit;

    public DDObjectFilterPhotoBiggerThen_1(int limit) {
        this.limit = limit;
    }

    // TODO add throws to the test function. The exception should be handled to the caller.
    @Override
    public boolean test(Object value) {
        PhotoWorker pw = (PhotoWorker) value;
        try {
            return pw.getPhotoInBytes().length > limit;
        } catch (IOException e) {
            return false;
        }
    }

}
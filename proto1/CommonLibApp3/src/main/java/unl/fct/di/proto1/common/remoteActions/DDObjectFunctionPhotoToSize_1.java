package unl.fct.di.proto1.common.remoteActions;

import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by AT DR on 16/03/2015.
 */

public class DDObjectFunctionPhotoToSize_1 implements Function<Object, Object>, Serializable {
    public Object apply(Object value) {
        // receive photo
        //photo or photoworker or photoMsg???
        PhotoWorker p = (PhotoWorker)value;
        try {
            return new Integer( p.getPhotoInBytes().length);
        } catch (IOException e) {
          //  e.printStackTrace(); // check if anothe print is more usefull
            return new Integer(0); // or null

        }
    }
}




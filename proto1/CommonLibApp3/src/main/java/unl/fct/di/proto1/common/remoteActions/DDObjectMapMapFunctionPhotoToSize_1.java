package unl.fct.di.proto1.common.remoteActions;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;

import java.io.Serializable;

/**
 * Created by AT DR on 16/03/2015.
 *
 */
public class DDObjectMapMapFunctionPhotoToSize_1 implements MapFunction<IPhotoRemote, Integer>, Serializable {
    public Integer apply(IPhotoRemote photo) {
        try {
            return  photo.getPhotoInBytes().length;
        } catch (Exception e) {
            //  e.printStackTrace();
            return 0;
        }
    }
}




package unl.fct.di.proto1.common.lib.core.services.photo;

import java.io.Serializable;

/**
 * Created by AT DR on 25-03-2015.
 *
 */
public interface IPhotoWorker extends IPhotoRemote {

    IPhoto getPhotoObject() throws Exception;
    String getPathFileName();
}

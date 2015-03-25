package unl.fct.di.proto1.common.lib.core.services.photo;

import java.io.Serializable;

/**
 * Created by AT DR on 25-03-2015.
 *
 */
public interface IPhotoRemote extends Serializable {
    String getPhotoUuid();

    byte[] getPhotoInBytes() throws Exception;

    byte[] getThumbnail() throws Exception;
}

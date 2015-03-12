package unl.fct.di.proto1.common.lib.core.services.photo;

import java.io.Serializable;

/**
 *
 *
 */
public interface IPhoto extends Serializable {

    String getPhotoUuid();

    Object getThumbnail();

    Object getPhoto();
}

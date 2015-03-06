package unl.fct.di.proto1.common.lib.core.services.photo;

import java.io.Serializable;

/**
 *
 *
 */
public interface IPhoto extends Serializable {

    String getUuid();

    Object getThumbnail();

    Object getPhoto();
}

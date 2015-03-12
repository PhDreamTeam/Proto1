package unl.fct.di.proto1.common.lib.core.services.photo;

import unl.fct.di.proto1.common.lib.protocol.services.MsgServicePhotoGetPhotoReply;

import java.io.Serializable;

/**
 *
 *
 */
public interface IPhoto extends Serializable {

    String getPhotoUuid();

    Object getThumbnail();

    Object getPhoto() throws Exception;

    public void fireMsgServicePhotoGetPhotoReply(MsgServicePhotoGetPhotoReply msg);
}

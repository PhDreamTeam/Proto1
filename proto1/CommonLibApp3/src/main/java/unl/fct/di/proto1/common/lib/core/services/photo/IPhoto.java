package unl.fct.di.proto1.common.lib.core.services.photo;

import unl.fct.di.proto1.common.lib.protocol.services.MsgServicePhotoGetPhotoReply;

/**
 *
 *
 */
public interface IPhoto extends IPhotoRemote {

    Object getPhoto() throws Exception;

    public void fireMsgServicePhotoGetPhotoReply(MsgServicePhotoGetPhotoReply msg);
}

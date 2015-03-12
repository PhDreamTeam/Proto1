package unl.fct.di.proto1.common.lib.protocol.services;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

/**
 * Created by AT DR on 12-03-2015.
 *
 */
public class MsgServicePhotoGetPhotoReply extends MsgReply {
    byte[] photo;

    public MsgServicePhotoGetPhotoReply(String photoUuid, String requestId, byte[] photo, boolean success, String failureReason) {
        super(photoUuid, requestId, success, failureReason);
        this.photo = photo;
    }

    public String getPhotoUuid() {
        return getDDUI();
    }

    public byte[] getPhoto() {
        return photo;
    }
}

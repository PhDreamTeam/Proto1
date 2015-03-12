package unl.fct.di.proto1.common.lib.protocol.services;

import unl.fct.di.proto1.common.lib.protocol.Msg;

/**
 * Created by AT DR on 12-03-2015.
 *
 */
public class MsgServicePhotoGetPhoto extends Msg {

    public MsgServicePhotoGetPhoto(String photoUuid, String requestId) {
        super(photoUuid, requestId);
    }

    public String getPhotoUuid() {
        return getDDUI();
    }
}

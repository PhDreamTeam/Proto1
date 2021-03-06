package unl.fct.di.proto1.common.lib.protocol.services;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

/**
 * Created by AT DR on 12-03-2015.
 *
 */
public class MsgServicePhotoGetPhotoReply extends MsgReply {
    byte[] photo;
    int msgPartNumber;
    int photoNumBytes;

    public MsgServicePhotoGetPhotoReply(String photoUuid, String requestId, int msgPartNumber,
                                        int photoNumBytes, byte[] photo, boolean success, String failureReason) {
        super(photoUuid, requestId, success, failureReason);
        this.photo = photo;
        this.msgPartNumber = msgPartNumber;
        this.photoNumBytes = photoNumBytes;
    }

    public String getPhotoUuid() {
        return getDDUI();
    }

    public byte[] getPhoto() {
        return photo;
    }

    public int getMsgPartNumber() {
        return msgPartNumber;
    }

    public int getPhotoNumBytes() {
        return photoNumBytes;
    }

    @Override
   public String getIntermediateInfo(){
        return "partNum: " + msgPartNumber + ", photoNumBytes: " + photoNumBytes;
    }

}

package pt.unl.fct.di.proto1.services.photos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.client.ClientManager;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhoto;
import unl.fct.di.proto1.common.lib.protocol.services.MsgServicePhotoGetPhoto;
import unl.fct.di.proto1.common.lib.protocol.services.MsgServicePhotoGetPhotoReply;
import java.util.HashMap;
import java.util.UUID;


// TODO
// TODO PHOTOINTERNAL getData return the photo, thumbnail by operation (conceptual - conceito)
// TODO getPhotoProxy...
// TODO next: more operations


// SOLUTION-CHECK: keep as it is - have a third class of PhotoINWOrker to deal with created DD in workers
// (in memory, on demand)


/**
 * Class that describes a photo to be used in client
 */
public class Photo implements IPhoto {
    // data from worker
    ActorNode workerActorNode;
    String photoUuid;
    byte[] thumbnail;
    byte[] photoInBytes;

    // data to be used by client
//    transient BufferedImage photo = null;  // only present after client call getPhoto to worker
    transient Bitmap photo = null;  // only present after client call getPhoto to worker
    String lastOperationError = null;
    MsgServicePhotoGetPhoto msgOut = null;


    public Photo(String photoUuid, byte[] thumbnail, ActorNode workerActorNode) {
        this.photoUuid = photoUuid;
        this.thumbnail = thumbnail;
        this.workerActorNode = workerActorNode;
    }

    @Override
    public String getPhotoUuid() {
        return photoUuid;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public byte[] getThumbnail() {
        return thumbnail;
    }

    public byte[] getPhotoInBytes() throws Exception {
        if (photoInBytes != null)
            return photoInBytes;

        // build worker actor ref
        if (workerActorNode.getActorRef() == null)
            workerActorNode.generateActorRef(ClientManager.getClientSystem());

        // send MsgGetPhoto to worker
        String requestId = UUID.randomUUID().toString();
        msgOut = new MsgServicePhotoGetPhoto(photoUuid, requestId);

        // save photo in client photo map
        ClientManager.putPhotoInPhotoMap(this);

        // send request to worker and show it in console
        workerActorNode.getActorRef().tell(msgOut, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msgOut + " to " + workerActorNode);

        // wait for data of DDInt
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        if (lastOperationError != null) {
            throw new RuntimeException(lastOperationError);
        }

        return photoInBytes;
    }

    @Override
    public Bitmap getPhoto() throws Exception {
        if (photo != null)
            return photo;

        if (photoInBytes == null)
            photoInBytes = getPhotoInBytes();


        // build photo from byte array
        photo = BitmapFactory.decodeByteArray(photoInBytes, 0, photoInBytes.length);
//        InputStream in = new ByteArrayInputStream(photoInBytes);
//        photo = ImageIO.read(in);
//        in.close();

        return photo;
    }

    HashMap<Integer, MsgServicePhotoGetPhotoReply> getPhotoReplyMsgs = new HashMap<>();
    int numberPhotoBytesReceived = 0;

    @Override
    public void fireMsgServicePhotoGetPhotoReply(MsgServicePhotoGetPhotoReply msg) {
        if (msg.isSuccess()) {

            getPhotoReplyMsgs.put(msg.getMsgPartNumber(), msg);

            if ((numberPhotoBytesReceived += msg.getPhoto().length) == msg.getPhotoNumBytes()) {
                try {
                    photoInBytes = getImageBytesFromMsgs();
                } catch (Exception e) {
                    ClientManager.getConsole().printException(e);
                    photoInBytes = null;
                    lastOperationError = "Error in getPhotoReply: " + e.getMessage();
                }

                // clean up memory structures
                getPhotoReplyMsgs.clear();
                numberPhotoBytesReceived = 0;

                // wake up client thread that asked to get Photo data
                synchronized (this) {
                    this.notify();
                }
            }
        } else {
            // no data
            photo = null;
            lastOperationError = "Error in getPhotoReply: " + msg.getFailureReason();

            // wake up client thread that asked to create DDInt
            synchronized (this) {
                this.notify();
            }
        }
    }

    private byte[] getImageBytesFromMsgs() throws Exception {
        int photoSize = getPhotoReplyMsgs.get(0).getPhotoNumBytes();

        // build photoBytes
        byte[] photoBytes = new byte[photoSize];

        // copy bytes from Msgs to this photobytes
        int currentIdx = 0;
        for (int i = 0, size = getPhotoReplyMsgs.size(); i < size; i++) {
            MsgServicePhotoGetPhotoReply msgAux = getPhotoReplyMsgs.get(i);
            System.arraycopy(msgAux.getPhoto(), 0, photoBytes, currentIdx, msgAux.getPhoto().length);
            currentIdx += msgAux.getPhoto().length;
        }

        if (currentIdx == photoSize) {
            ClientManager.getConsole().println("photo successfully received with " + currentIdx + " bytes");
            lastOperationError = null;
        } else throw new RuntimeException("Error Photo of unexpected size received, received: " + currentIdx +
                ", expected: " + photoSize + " bytes");

        return photoBytes;
    }

    @Override
    public String toString() {
        return "IPHOTO: " + photoUuid;
    }


}

package pt.unl.fct.di.proto1.services.photos;

import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.client.ClientManager;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhoto;
import unl.fct.di.proto1.common.lib.protocol.services.MsgServicePhotoGetPhoto;
import unl.fct.di.proto1.common.lib.protocol.services.MsgServicePhotoGetPhotoReply;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    // data to be used by client
    transient BufferedImage photo = null;  // only present after client call getPhoto to worker
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

    @Override
    public BufferedImage getPhoto() throws Exception {
        if(photo != null)
            return photo;

        // build worker actor ref
        if(workerActorNode.getActorRef() == null)
            workerActorNode.generateActorRef(ClientManager.getClientSystem());

        // send MsgGetPhoto to worker
        String requestId = UUID.randomUUID().toString();
        msgOut = new MsgServicePhotoGetPhoto(photoUuid, requestId);

        // save photo in client photo map
        ClientManager.putPhotoInPhotoMap(this);

        // send request to worker and show it in console
        workerActorNode.getActorRef().tell( msgOut, ClientManager.getClientActor());
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

        return photo;
    }

    @Override
    public void fireMsgServicePhotoGetPhotoReply(MsgServicePhotoGetPhotoReply msg) {
        if (msg.isSuccess()) {
            // build photo from byte array
            InputStream in = new ByteArrayInputStream(msg.getPhoto());
            try {
                photo = ImageIO.read(in);
                in.close();
                lastOperationError = null;
            } catch (IOException e) {
                ClientManager.getConsole().printException(e);
                photo = null;
                lastOperationError = "Error in getPhotoReply: " + e.getMessage();
            }
        } else {
            // no data
            photo = null;
            lastOperationError = "Error in getPhotoReply: " + msg.getFailureReason();
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public String toString() {
        return "IPHOTO: " + photoUuid;
    }


}

package pt.unl.fct.di.proto1.services.photos;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoWorker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class PhotoWorker implements IPhotoWorker {
    String uuid;
    String pathFileName;

    // photo thumbnail
    byte[] thumbnail = null;

    // photo itself
    //transient BufferedImage photo = null;  // only present after getPhoto
    transient Bitmap photo = null;  // only present after getPhoto

     // object that represents the photo to be sent to the client
    Photo photoClient = null;

    ActorNode workerActorNode;


    public PhotoWorker(String uuid, String pathFileName, ActorNode workerActorNode) {
        this.uuid = uuid;
        this.pathFileName = pathFileName;
        this.workerActorNode = workerActorNode;
    }

    public String getPhotoUuid() {
        return uuid;
    }

    public String getPathFileName() {
        return pathFileName;
    }

    public byte[] getThumbnail() throws Exception {
        if (thumbnail != null)
            return thumbnail;

        photo = getPhoto();
        int width = photo.getWidth();
        int height = photo.getHeight();

        int newWidth = width < height ? (width * 100) / height : 100;
        int newHeight = width < height ? 100 :  (height * 100) / width;


        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
        thumbnail = baos.toByteArray();

//        BufferedImage bi = Scalr.resize(photo, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, newWidth, newHeight, Scalr.OP_ANTIALIAS);
//        // generate byte[]
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(bi, "jpg", baos );
//        thumbnail = baos.toByteArray();

        return thumbnail;
    }

    // TODO ler directamente do ficheiro, sem utiliar o BufferedImage
    private void loadPhoto() throws IOException {
        //photo = ImageIO.read(new File(pathFileName));
        photo = BitmapFactory.decodeFile(pathFileName);
        // TODO console.println("loading image from: " + pathFileName);
    }

    public Bitmap /*BufferedImage*/ getPhoto() throws IOException {
        if(photo == null)
            loadPhoto();
        return photo;
    }

    public byte[] getPhotoInBytes()throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(getPhoto(), "jpg", baos );
        photo.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
        return baos.toByteArray();
    }

    public Photo getPhotoObject() throws Exception {
        if(photoClient == null)
            photoClient = new Photo(uuid, getThumbnail(), workerActorNode);
        return photoClient;
    }
}

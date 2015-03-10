package pt.unl.fct.di.proto1.services.photos;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 */
public class PhotoWorker implements Serializable {
    String uuid;
    String pathFileName;

    // photo thumbnail
    BufferedImage thumbnail = null;

    // photo itself
    BufferedImage photo = null;  // only present after getPhoto

     // object that represents the photo to be sent to the client
    Photo photoClient = null;


    public PhotoWorker(String uuid, String pathFileName) {
        this.uuid = uuid;
        this.pathFileName = pathFileName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPathFileName() {
        return pathFileName;
    }

    public BufferedImage getThumbnail() throws IOException {
       if(thumbnail == null)
           thumbnail = generateThumbnail();
        return thumbnail;
    }

    private BufferedImage generateThumbnail() throws IOException {
        if (thumbnail != null)
            return thumbnail;

        if (photoClient != null){
            BufferedImage thumb = photoClient.getThumbnail();
            if (thumb != null)
                return thumb;
        }

        if (photo == null) {
            photo = getPhoto();
        }

        int width = photo.getWidth();
        int height = photo.getHeight();

        int newWidth = width < height ? (width * 100) / height : 100;
        int newHeight = width < height ? 100 :  (height * 100) / width;
        thumbnail = Scalr.resize(photo, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, newWidth, newHeight, Scalr.OP_ANTIALIAS);
        return thumbnail;
    }

    private void loadPhoto() throws IOException {
        photo = ImageIO.read(new File(pathFileName));
        // TODO console.println("loading image from: " + pathFileName);
    }

    public BufferedImage getPhoto() throws IOException {
        if(photo == null)
            loadPhoto();
        return photo;
    }

    public Photo  getPhotoObject() throws IOException {
        if(photoClient == null)
            photoClient = new Photo(getPathFileName(), getThumbnail());
        return photoClient;
    }
}

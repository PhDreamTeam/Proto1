package pt.unl.fct.di.proto1.services.photos;

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

    public BufferedImage getThumbnail() {
       if(thumbnail == null)
           thumbnail = generateThumbnail();
        return thumbnail;
    }

    private BufferedImage generateThumbnail() {
        // TODO generate thumbnail
        return null;
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

    public Photo getPhotoObject(){
        if(photoClient == null)
            photoClient = new Photo(getPathFileName(), getThumbnail());
        return photoClient;
    }
}

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
        // testar se é portrait ou landscape
        int width = photo.getWidth();
        int height = photo.getHeight();
        if (width < height) {
            float extraSize = height - 100;
            float percentHeight = (extraSize / height) * 100;
            int percentWidth = (new Float(width - ((width / 100) * percentHeight))).intValue();
            thumbnail = Scalr.resize(photo, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, percentWidth, 100, Scalr.OP_ANTIALIAS);
            // check if it's better to use simple .getScaledInstance(100, 100, Image.SCALE_FAST)
        } else {
            float extraSize = width - 100;
            float percentWidth = (extraSize / width) * 100;
            int percentHeight = (new Float(height - ((height / 100) * percentWidth))).intValue();
            thumbnail = Scalr.resize(photo, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, 100, percentHeight, Scalr.OP_ANTIALIAS);
        }
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

    public Photo getPhotoObject() throws IOException {
        if(photoClient == null)
            photoClient = new Photo(getPathFileName(), getThumbnail());
        return photoClient;
    }
}

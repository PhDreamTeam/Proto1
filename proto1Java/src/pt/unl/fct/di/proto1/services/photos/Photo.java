package pt.unl.fct.di.proto1.services.photos;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhoto;

import java.awt.image.BufferedImage;

/**
 * Class that describes a photo to be used in client
 *
 */

public class Photo implements IPhoto {
    String photoUuid;
    byte[] thumbnail;
    transient BufferedImage photo = null;  // only present after client call getPhoto to worker

    public Photo(String photoUuid, byte[] thumbnail) {
        this.photoUuid = photoUuid;
        this.thumbnail = thumbnail;
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
    public BufferedImage getPhoto() {
        throw new RuntimeException("To be implemented");
        //return null;
    }

    @Override
    public String toString() {
        return "IPHOTO: " + photoUuid;
    }
}

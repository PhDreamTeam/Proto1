package pt.unl.fct.di.proto1.services.photos;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhoto;

import java.awt.image.BufferedImage;

/**
 * Class that describes a photo to be used in client
 *
 */
public class Photo implements IPhoto {
    String uuid;
    BufferedImage thumbnail;
    BufferedImage photo = null;  // only present after getPhoto

    public Photo(String uuid, BufferedImage thumbnail) {
        this.uuid = uuid;
        this.thumbnail = thumbnail;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setThumbnail(BufferedImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    @Override
    public BufferedImage getPhoto() {
        return null;
    }

    @Override
    public String toString() {
        return "IPHOTO: " + uuid;
    }
}

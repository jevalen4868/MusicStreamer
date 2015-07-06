package com.fallenman.apps.musicstreamer.connector;

import android.graphics.Bitmap;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 */
public class MusicVo {
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * holds the name of the artist, band, ensemble, etc..
     */
    private String entityName;

    public String getEntityImageUrl() {
        return entityImageUrl;
    }

    public void setEntityImageUrl(String entityImageUrl) {
        this.entityImageUrl = entityImageUrl;
    }

    /**
     * Will hold the image url of the artist, band, ensemble, etc..
     */
    private String entityImageUrl;

    public Bitmap getEntityImage() {
        return entityImage;
    }

    public void setEntityImage(Bitmap entityImage) {
        this.entityImage = entityImage;
    }

    /**
     * Will hold the image url of the artist, band, ensemble, etc..
     */
    private Bitmap entityImage;

}

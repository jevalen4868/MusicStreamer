package com.fallenman.apps.musicstreamer.connector;

import android.media.Image;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 */
public class MusicVO {
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
     * Will hold the image of the artist, band, ensemble, etc..
     */
    private String entityImageUrl;
}

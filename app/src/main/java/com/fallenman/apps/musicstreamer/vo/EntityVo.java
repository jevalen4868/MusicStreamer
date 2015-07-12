package com.fallenman.apps.musicstreamer.vo;

import android.graphics.Bitmap;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 */
public class EntityVo {
    /**
     * holds the name of the artist, band, ensemble, etc..
     */
    private String entityName;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Will hold the image url of the artist, band, ensemble, etc..
     */
    private String entityImageUrl;

    public String getEntityImageUrl() {
        return entityImageUrl;
    }

    public void setEntityImageUrl(String entityImageUrl) {
        this.entityImageUrl = entityImageUrl;
    }

    /**
     * Whatever the id of the entity is for the application
      */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

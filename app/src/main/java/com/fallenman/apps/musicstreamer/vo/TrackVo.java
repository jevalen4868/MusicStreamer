package com.fallenman.apps.musicstreamer.vo;

/**
 * Created by jeremyvalenzuela on 7/7/15.
 */
public class TrackVo {

    private String entityNames;
    private String trackName;
    private String albumName;
    private String imageUrl;
    private String previewUrl;
    private long duration;

    public String getEntityNames() {
        return entityNames;
    }

    public void setEntityNames(String entityNames) {
        this.entityNames = entityNames;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}

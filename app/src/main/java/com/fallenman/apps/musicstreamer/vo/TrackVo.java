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

//    // Make sure to add any new values here.
//    @Override
//    public boolean equals(Object o) {
//        TrackVo tVo = (TrackVo)o;
//        // compare each field for equality.
//        if( entityNames.contentEquals(tVo.entityNames)
//                && trackName.contentEquals(tVo.trackName)
//                && albumName.contentEquals(tVo.albumName)
//                && imageUrl.contentEquals(tVo.imageUrl)
//                && previewUrl.contentEquals(tVo.previewUrl)
//                && duration == tVo.duration)
//        {
//            return true;
//        }
//        // Not a match!
//        return false;
//    }

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

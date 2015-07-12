package com.fallenman.apps.musicstreamer.connector;

import com.fallenman.apps.musicstreamer.vo.EntityVo;
import com.fallenman.apps.musicstreamer.vo.TrackVo;

import java.util.List;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 * Every request for data shall be retrieved through this connector.
 */
public interface MusicConnector {
    /**
     * Default list
     * @param query
     * @return list of musicVo to display in main activity.
     */
    public List<EntityVo> getEntityVoList(String query);

    /**
     * Process for getting list of tracks for entity.
     * @param query
     * @return list of tracks based on param.
     */
    public List<TrackVo> getTopTrackVoList(String query);

}

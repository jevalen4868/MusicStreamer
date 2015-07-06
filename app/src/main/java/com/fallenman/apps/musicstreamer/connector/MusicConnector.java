package com.fallenman.apps.musicstreamer.connector;

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
    public List<MusicVo> getMusicVoList(String query);
}

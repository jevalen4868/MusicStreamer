package com.fallenman.apps.musicstreamer.factory;

import com.fallenman.apps.musicstreamer.connector.MusicConnector;
import com.fallenman.apps.musicstreamer.connector.SpotifyConnector;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 */
public class MusicFactory {
    private static MusicConnector defaultConnector;

    /**
     * For now, the default connector is Spotify.
     * @return the default connector.
     */
    private static MusicConnector getDefaultConnector() {
        return new SpotifyConnector();
    }

    /**
     * lazy initialization of connector.
     * @return
     */
    public static MusicConnector getConnector() {
        if(defaultConnector == null) {
            synchronized (MusicConnector.class) {
                if (defaultConnector == null) {
                    defaultConnector = getDefaultConnector();
                }
            }
        }
        return defaultConnector;
    }
}

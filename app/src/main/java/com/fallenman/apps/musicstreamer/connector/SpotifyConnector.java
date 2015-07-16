package com.fallenman.apps.musicstreamer.connector;

import android.util.Log;

import com.fallenman.apps.musicstreamer.vo.EntityVo;
import com.fallenman.apps.musicstreamer.vo.TrackVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 * The spotify api works via http / restful services requests.
 */
public class SpotifyConnector implements MusicConnector {
    private static final String LOG_TAG = SpotifyConnector.class.getSimpleName();

    private SpotifyApi api;
    private SpotifyService svc;

    public SpotifyConnector() {
        api = new SpotifyApi();
        svc = api.getService();
    }

    /**
     * @param entityName - Entity name
     * @return list VO of all the artists, ids and their images.
     */
    @Override
    public List<EntityVo> getEntityVoList(String entityName) {
        List<EntityVo> eVoList = new ArrayList<EntityVo>(10);
        ArtistsPager ap = null;
        try {
            // Utilize the spotify api for data retrieval.
            ap = svc.searchArtists(entityName);
        }
        catch( RetrofitError ex ) {
            Log.e(LOG_TAG, "ERROR", ex);
            return null;
        }
        Pager<Artist> pager = ap.artists;
        for(Artist artist : pager.items) {
            // Start adding items to our musicVO
            EntityVo eVo = new EntityVo();
            eVo.setEntityName(artist.name);
            eVo.setId(artist.id);
            // It seems that not every artist has an image.
            if( ! artist.images.isEmpty() ) {
                for (Image image : artist.images) {
                    if (image.height == 300) {
                        eVo.setEntityImageUrl(image.url);
                    }
                }
                // If no image matched, just set to the first image.
                if(eVo.getEntityImageUrl() == null) {
                    eVo.setEntityImageUrl(artist.images.get(0).url);
                }
            }
            eVoList.add(eVo);
        }
        // That was easy ;)
        return eVoList;
    }

    /**
     * Return track Vo containing track data.
     * @param query
     * @return artists top tracks
     */
    @Override
    public List<TrackVo> getTopTrackVoList(String query) {
        List<TrackVo> tVoList = new ArrayList<TrackVo>(10);
        // Query by country code for US by default.
        Map queryParams = new HashMap<String, String>();
        queryParams.put("country", "US");
        // Utilize spotify api again!
        Tracks tracks = null;
        try {
            tracks = svc.getArtistTopTrack(query, queryParams);
        }
        catch( RetrofitError ex ) {
            Log.e(LOG_TAG, "ERROR", ex);
            // No net connection, return null;
            return null;
        }
        for (Track t : tracks.tracks) {
            TrackVo tVo = new TrackVo();
            tVo.setAlbumName(t.album.name);
            tVo.setPreviewUrl(t.preview_url);
            tVo.setTrackName(t.name);
            // Image attr setting.
            List<Image> albumImages = t.album.images;
            if( ! albumImages.isEmpty()) {
                // Let's check if the size is 640px, if so, that's what we want for our album art.
                for ( Image albumImage : albumImages ) {
                    // 300 for thumbnails
                    if ( albumImage.width == 300) {
                        tVo.setImageUrl(albumImage.url);
                    }
                }
                // If we don't have a match, just set both to the first image.
                if(tVo.getImageUrl() == null) {
                    tVo.setImageUrl(albumImages.get(0).url);
                }
            }
            // Finally, add to list of tVos!
            tVoList.add(tVo);
        }
        return tVoList;
    }
}

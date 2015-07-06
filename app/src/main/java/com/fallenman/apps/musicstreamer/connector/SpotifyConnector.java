package com.fallenman.apps.musicstreamer.connector;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 * The spotify api works via http / restful services requests.
 */
public class SpotifyConnector implements MusicConnector {
    /**
     * @param entityName - Entity name
     * @return list VO of all the artists and their corresponding images..
     */
    @Override
    public List<MusicVo> getMusicVoList(String entityName) {
        List<MusicVo> mVoList = new ArrayList<MusicVo>(10);
        // Utilize the spotify api for data retrieval.
        SpotifyApi api = new SpotifyApi();
        SpotifyService svc = api.getService();
        ArtistsPager ap = svc.searchArtists(entityName);
        Pager<Artist> pager = ap.artists;
        for(Artist artist : pager.items) {
            // Start adding items to our musicVO
            MusicVo mVo = new MusicVo();
            mVo.setEntityName(artist.name);
            // It seems that not every artist has an image.
            if( ! artist.images.isEmpty()) {
                mVo.setEntityImageUrl(artist.images.get(0).url);
            }
            mVoList.add(mVo);
        }
        // That was easy ;)
        return mVoList;
    }
}

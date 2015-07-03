package com.fallenman.apps.musicstreamer.connector;

import android.net.Uri;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
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
    public List<MusicVO> getMusicVoList(String entityName) {
        List<MusicVO> mVoList = new ArrayList<MusicVO>(10);
        // Utilize the spotify api for data retrieval.
        SpotifyApi api = new SpotifyApi();
        SpotifyService svc = api.getService();
        ArtistsPager ap = svc.searchArtists(entityName);
        Pager<Artist> pager = ap.artists;
        for(Artist artist : pager.items) {
            // Start adding items to our musicVO
            MusicVO mVo = new MusicVO();
            mVo.setEntityName(artist.name);
            mVo.setEntityImageUrl(artist.images.get(0).url);
            mVoList.add(mVo);
        }
        // That was easy ;)
        return mVoList;
    }
}

package com.fallenman.apps.musicstreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fallenman.apps.musicstreamer.adapter.TrackAdapter;
import com.fallenman.apps.musicstreamer.connector.MusicConnector;
import com.fallenman.apps.musicstreamer.factory.MusicFactory;
import com.fallenman.apps.musicstreamer.utilities.Display;
import com.fallenman.apps.musicstreamer.vo.TrackVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private static final String LOG_TAG = TopTracksActivityFragment.class.getSimpleName();
    private TrackAdapter mTrackAdapter;
    private String mEntityId;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        // Grab calling intent, so we can take it's data! Muahahaha!
        Intent intent = getActivity().getIntent();
        // check if intent has the data we are expecting.
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            this.mEntityId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        // Now initialize track adapter.
        mTrackAdapter = new TrackAdapter(getActivity(), R.layout.track_layout, new ArrayList<TrackVo>(10));
        // Get the forecast list view to set forecast array adapter on.
        ListView trackListView = (ListView)rootView.findViewById(R.id.listView_trackData);
        trackListView.setAdapter(mTrackAdapter);
        trackListView.setOnItemClickListener(new TrackDataOnClickListener());
        // Voila!!!
        return rootView;
    }

    @Override
    public void onStart() {
        (new FetchTrackDataTask()).execute(this.mEntityId);
        super.onStart();
    }

    // PLEASE DEFINE SUB CLASSES BELOW THIS COMMENT.////////////////////////////////////////////////

    public class FetchTrackDataTask extends AsyncTask<String, Void, List<TrackVo>> {

        @Override
        protected List<TrackVo> doInBackground(String... params) {
            if(params == null || params.length == 0) {
                // Nothing to do, exit.
                return null;
            }
            // Check parameter 0 - entityname.
            if(params[0].contentEquals("")) {
                // Nothing to do.
                return null;
            }
            String entityId = params[0];
            // Our mEntityId should be set, let's retrieve the artists top tracks
            // Let's utilize our fancy MusicConnector.
            MusicFactory mf = new MusicFactory();
            MusicConnector mc = mf.getConnector();
            List<TrackVo> tVoList = mc.getTopTrackVoList(entityId);
            // Badabing! We should be able to just set this to mTrackAdapter.
            return tVoList;
        }

        @Override
        protected void onPostExecute(List<TrackVo> trackVoList) {
            super.onPostExecute(trackVoList);
            mTrackAdapter.clear();
            if ( trackVoList == null || trackVoList.isEmpty()) {
                Toast noDataToast = Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT);
                noDataToast.show();
            }
            else // Add all tracks to adapter.
            {
                mTrackAdapter.addAll(trackVoList);
            }
        }
    }

    private class TrackDataOnClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            // Get the current preview url.
            TrackVo tVo = (TrackVo) adapterView.getItemAtPosition(position);
            String playerJsonStr = getPlayerJson(tVo).toString();
            // Executed in an Activity, so 'getActivity' is the Context
            Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
            // Stick some "extra text" on it. The json string to populate and play in the player.
            playerIntent.putExtra(Intent.EXTRA_TEXT, playerJsonStr);
            // Start the intent.
            getActivity().startActivity(playerIntent);
        }
    }

    /** Will use passed in  tVo to construct JSON for passing to
     * PlayerActivity.
     * @param tVo
     * @return player json object
     */
    private JSONObject getPlayerJson(TrackVo tVo) {
        JSONObject playerJson = new JSONObject();
        try {
            // main artist call id.
            playerJson.put("entityId", mEntityId);
            // track attributes.
            JSONObject attrs = new JSONObject();
            attrs.put("entityNames", tVo.getEntityNames());
            attrs.put("trackName", tVo.getTrackName());
            attrs.put("albumName", tVo.getAlbumName());
            attrs.put("imageUrl", tVo.getImageUrl());
            attrs.put("previewUrl", tVo.getPreviewUrl());
            playerJson.put("attributes", attrs);
        } catch (JSONException je) {
            Log.e(LOG_TAG, "ERROR", je);
        }
        return playerJson;
    }
}

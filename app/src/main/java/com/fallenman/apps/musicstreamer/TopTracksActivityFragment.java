package com.fallenman.apps.musicstreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenman.apps.musicstreamer.adapter.MusicAdapter;
import com.fallenman.apps.musicstreamer.adapter.TrackAdapter;
import com.fallenman.apps.musicstreamer.connector.MusicConnector;
import com.fallenman.apps.musicstreamer.factory.MusicFactory;
import com.fallenman.apps.musicstreamer.vo.EntityVo;
import com.fallenman.apps.musicstreamer.vo.TrackVo;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    public TopTracksActivityFragment() {
    }

    private TrackAdapter trackAdapter;
    private String entityId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        // Grab calling intent, so we can take it's data! Muahahaha!
        Intent intent = getActivity().getIntent();
        // check if intent has the data we are expecting.
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            this.entityId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        // Now initialize track adapter.
        trackAdapter = new TrackAdapter(getActivity(), R.layout.track_layout, new ArrayList<TrackVo>(10));
        // Get the forecast list view to set forecast array adapter on.
        ListView trackListView = (ListView)rootView.findViewById(R.id.listView_trackData);
        trackListView.setAdapter(trackAdapter);
        // trackListView.setOnItemClickListener( new EntityDataOnClickListener() );
        // Voila!!!
        return rootView;
    }

    @Override
    public void onStart() {
        (new FetchTrackDataTask()).execute(this.entityId);
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
            // Our entityId should be set, let's retrieve the artists top tracks
            // Let's utilize our fancy MusicConnector.
            MusicConnector mc = MusicFactory.getConnector();
            List<TrackVo> tVoList = mc.getTopTrackVoList(entityId);
            // Badabing! We should be able to just set this to trackAdapter.
            return tVoList;
        }

        @Override
        protected void onPostExecute(List<TrackVo> trackVoList) {
            super.onPostExecute(trackVoList);
            trackAdapter.clear();
            if ( ! trackVoList.isEmpty()) {
                trackAdapter.addAll(trackVoList);
            }
            // Display toast if no data returned.
            if(trackVoList.isEmpty()) {
                Toast noDataToast = Toast.makeText(getActivity(), "No data found!" , Toast.LENGTH_SHORT);
                noDataToast.show();
            }
        }
    }
}

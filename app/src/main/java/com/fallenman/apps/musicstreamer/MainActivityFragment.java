package com.fallenman.apps.musicstreamer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.connector.MusicConnector;
import com.fallenman.apps.musicstreamer.connector.MusicVo;
import com.fallenman.apps.musicstreamer.factory.MusicFactory;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private EditText entityName;
    private MusicAdapter musicAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Instantiate the music adapter, this will do our magic!
        musicAdapter = new MusicAdapter(getActivity(), R.layout.layout_main, new ArrayList<MusicVo>(10));
        // Get the forecast list view to set forecast array adapter on.
        ListView forecastListView = (ListView)rootView.findViewById(R.id.listView_entityData);
        forecastListView.setAdapter(musicAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        // Instead of using anonymous inner class, I prefer to define it.
        // Retrieve text from view upon enter.
        ArtistNameListener anl = new ArtistNameListener();
        entityName = (EditText) getActivity().findViewById(R.id.editText_artist);
        entityName.setOnEditorActionListener(anl);
        super.onStart();
    }

    // PLEASE DEFINE SUB CLASSES BELOW THIS COMMENT.////////////////////////////////////////////////

    public class ArtistNameListener implements TextView.OnEditorActionListener
    {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            // Use input method manager to close the keyboard on enter.
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    entityName.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            // Now request data from MusicConnector.
            CharSequence entityQueryName = textView.getText();
            FetchEntityDataTask fedt = new FetchEntityDataTask();
            fedt.execute(entityQueryName.toString());
            return true;
        }
    }

    public class FetchEntityDataTask extends AsyncTask<String, Void, List<MusicVo>> {

        @Override
        protected List<MusicVo> doInBackground(String... params) {
            if(params == null || params.length == 0) {
                // Nothing to do, exit.
                return null;
            }
            // Check parameter 0 - entityname.
            if(params[0].contentEquals("")) {
                // Nothing to do.
                return null;
            }
            String entityQueryName = params[0];
            MusicConnector connector = MusicFactory.getConnector();
            List<MusicVo> musicVoList = connector.getMusicVoList(entityQueryName.toString());
            // Now attempt to fetch the image via Picasso.
            for(MusicVo mVo : musicVoList) {
                try {
                    mVo.setEntityImage(Picasso.with(getActivity()).load(mVo.getEntityImageUrl()).get());
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, "ERROR=", ioe);
                }
            }
            return musicVoList;
        }

        @Override
        protected void onPostExecute(List<MusicVo> musicVoList) {
            super.onPostExecute(musicVoList);
            musicAdapter.clear();
            for(MusicVo mVo : musicVoList) {
                musicAdapter.add(mVo);
            }
        }
    }
}

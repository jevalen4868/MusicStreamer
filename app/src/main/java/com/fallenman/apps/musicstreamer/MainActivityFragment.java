package com.fallenman.apps.musicstreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.fallenman.apps.musicstreamer.adapter.MusicAdapter;
import com.fallenman.apps.musicstreamer.connector.MusicConnector;
import com.fallenman.apps.musicstreamer.utilities.DisplayFunctions;
import com.fallenman.apps.musicstreamer.utilities.NetworkFunctions;
import com.fallenman.apps.musicstreamer.vo.EntityVo;
import com.fallenman.apps.musicstreamer.factory.MusicFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private SearchView searchEntityName;
    private MusicAdapter musicAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Instantiate the music adapter, this will do our magic!
        musicAdapter = new MusicAdapter(getActivity(), R.layout.layout_main, new ArrayList<EntityVo>(10));
        // Get the forecast list view to set forecast array adapter on.
        ListView forecastListView = (ListView)rootView.findViewById(R.id.listView_entityData);
        forecastListView.setAdapter(musicAdapter);
        forecastListView.setOnItemClickListener(new EntityDataOnClickListener());
        return rootView;
    }

    @Override
    public void onStart() {
        // Instead of using anonymous inner class, I prefer to define it.
        // Retrieve text from view upon enter.
        searchEntityName = (SearchView) getActivity().findViewById(R.id.searchText_entity);
        searchEntityName.setOnQueryTextListener(new EntityNameListener());
        searchEntityName.setIconified(false);
        super.onStart();
    }

    // PLEASE DEFINE SUB CLASSES BELOW THIS COMMENT.////////////////////////////////////////////////

    public class EntityDataOnClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            EntityVo eVo = musicAdapter.getItem(pos);
            // Executed in an Activity, so 'getActivity' is the Context
            Intent detailIntent = new Intent(getActivity(), TopTracksActivity.class);
            // Stick some "extra text" on it. The id of the artist.
            detailIntent.putExtra(Intent.EXTRA_TEXT, eVo.getId());
            // Start the intent.
            getActivity().startActivity(detailIntent);
        }
    }
    public class EntityNameListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // Don't do anything if there's no network.
            if( ! NetworkFunctions.isNetworkAvailable(getActivity())) {
                DisplayFunctions.shortToast(getActivity(), "No network available!");
                return false;
            }
            // Use input method manager to close the keyboard on enter.
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    searchEntityName.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            // Now request data from MusicConnector.
            CharSequence entityQueryName = query;
            FetchEntityDataTask fedt = new FetchEntityDataTask();
            fedt.execute(entityQueryName.toString());
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    }

    public class FetchEntityDataTask extends AsyncTask<String, Void, List<EntityVo>> {
        @Override
        protected List<EntityVo> doInBackground(String... params) {
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
            MusicFactory mf = new MusicFactory();
            MusicConnector connector = mf.getConnector();
            List<EntityVo> entityVoList = connector.getEntityVoList(entityQueryName.toString());
            return entityVoList;
        }

        @Override
        protected void onPostExecute(List<EntityVo> entityVoList) {
            super.onPostExecute(entityVoList);
            musicAdapter.clear();
            // Display toast if no data returned.
            if(entityVoList == null || entityVoList.isEmpty()) {
                DisplayFunctions.shortToast(getActivity(), "No data found!");
            }
            else // we have data!
            {
                for (EntityVo mVo : entityVoList) {
                    musicAdapter.add(mVo);
                }
            }
        }
    }
}

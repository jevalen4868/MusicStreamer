package com.fallenman.apps.musicstreamer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fallenman.apps.musicstreamer.constants.Main;
import com.fallenman.apps.musicstreamer.constants.PlayerJson;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TOP_TRACKS_FRAGMENT_TAG = "TOP_TRACKS_FRAGMENT_TAG";
    private boolean mIsLargeLayout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.top_tracks_container) != null) {
            //We've hit our large layout file.
            mIsLargeLayout = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, new TopTracksActivityFragment(), TOP_TRACKS_FRAGMENT_TAG)
                        .commit();
            }
        } else { //we are on a small phone.
            mIsLargeLayout = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Intent getParentActivityIntent() {
        // add the clear top flag - which checks if the parent (main)
        // activity is already running and avoids recreating it
        return super.getParentActivityIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onItemSelected(String entityId) {
        //Prepare data.
        Bundle data = new Bundle();
        data.putString(Main.ENTITY_ID, entityId);
        Log.d(LOG_TAG, String.valueOf(mIsLargeLayout));
        if (mIsLargeLayout) {

            // Begin fragment processing.
            TopTracksActivityFragment topTracksActivityFragment = new TopTracksActivityFragment();
            topTracksActivityFragment.setArguments(data);
            // The device is using a large layout, so show the fragment in the window.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, topTracksActivityFragment, TOP_TRACKS_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        }
    }
}
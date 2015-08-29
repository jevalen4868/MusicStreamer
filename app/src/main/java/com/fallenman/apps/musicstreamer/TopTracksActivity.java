package com.fallenman.apps.musicstreamer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fallenman.apps.musicstreamer.constants.PlayerJson;


public class TopTracksActivity extends ActionBarActivity implements TopTracksActivityFragment.Callback{
    private static final String LOG_TAG = TopTracksActivity.class.getSimpleName();
    private boolean mIsLargeLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        if (savedInstanceState == null) {
            // Create the detail fragment using a transaction.
            Bundle args = getIntent().getExtras();

            TopTracksActivityFragment topTracksActivityFragment = new TopTracksActivityFragment();
            topTracksActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_top_tracks_container, topTracksActivityFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
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

    public TopTracksActivity() {
        super();
    }

    @Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Intent getParentActivityIntent() {
    // add the clear top flag - which checks if the parent (main)
    // activity is already running and avoids recreating it
        return super.getParentActivityIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onItemSelected(String json) {
        //Prepare data.
        Bundle data = new Bundle();
        data.putString(PlayerJson.PLAYER_JSON_BUNDLE_ID, json);
        Log.d(LOG_TAG, String.valueOf(mIsLargeLayout));
        if (mIsLargeLayout) {

            // Begin fragment processing.
            FragmentManager fragmentManager = getSupportFragmentManager();
            PlayerActivityFragment playerActivityFragment = new PlayerActivityFragment();
            playerActivityFragment.setArguments(data);
            // The device is using a large layout, so show the fragment as a dialog
            playerActivityFragment.show(fragmentManager, "dialog");
        } else {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        }
    }
}

package com.fallenman.apps.musicstreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.utilities.Display;

import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private TextView entityNames;

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        // Grab calling intent, so we can take it's data! Muahahaha!
        Intent intent = getActivity().getIntent();
        // check if intent has the data we are expecting.
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String playerJsonStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            Display.shortToast(getActivity(), playerJsonStr);
        }
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
}

package com.fallenman.apps.musicstreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.constants.PlayerJson;
import com.fallenman.apps.musicstreamer.utilities.CompatibilityImageFunctions;
import com.fallenman.apps.musicstreamer.utilities.DisplayFunctions;
import com.fallenman.apps.musicstreamer.utilities.NetworkFunctions;
import com.fallenman.apps.musicstreamer.vo.TrackVo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 * Some snippets from
 * http://stackoverflow.com/questions/22438861/showing-error-while-trying-to-run-mediaplayer-in-asynctask
 */
public class PlayerActivityFragment extends Fragment implements MediaPlayer.OnPreparedListener {
    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    // All variables regarding current track playing.
    private ScrollView mScrollView;
    private String mEntityId;
    private TextView mEntityNames;
    private TextView mTrackName;
    private TextView mAlbumName;
    private ResizableImageView mAlbumArt;
    private SeekBar mSeekBar;
    private ImageButton mPlayPauseTrack;
    private ImageButton mNextTrack;
    private ImageButton mPreviousTrack;
    // A list which contains all the mTracks pass in.
    private List<TrackVo> mTracks = new ArrayList<TrackVo>();

    // Variables for tracking the current track.
    private TrackVo mCurrentTrack;
    private int mCurrentTrackPosition;

    public PlayerActivityFragment() {
    }

    /**
     * Expecting json in this format:
     * {"entityId":"085pc2PYOi8bGKj0PNjekA",
     * "mTracks":
     * [{"entityNames":"will.i.am, Britney Spears",
     * "trackName":"Scream & Shout",
     * "albumName":"#willpower",
     * "imageUrl":"https:\/\/i.scdn.co\/image\/3427485b184fd18625c88c8c1d2748697981f1c0",
     * "previewUrl":"https:\/\/p.scdn.co\/mp3-preview\/b0d699e5bb555afd96adac088adeb963b27b0895",
     * "selected":true}]
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        mScrollView = (ScrollView) rootView.findViewById(R.id.player_fragment_scrollView);
        // Now do all the background noise...
        mEntityNames = (TextView) rootView.findViewById(R.id.entity_names_textView);
        mAlbumName = (TextView) rootView.findViewById(R.id.album_name_textView);
        mAlbumArt = (ResizableImageView) rootView.findViewById(R.id.album_art_imageView);
        mTrackName = (TextView) rootView.findViewById(R.id.track_name_textView);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.player_seek_bar);
        mPreviousTrack = (ImageButton) rootView.findViewById(R.id.previous_imageButton);
        mPlayPauseTrack = (ImageButton) rootView.findViewById(R.id.play_pause_imageButton);
        mPlayPauseTrack.setImageResource(android.R.drawable.ic_media_pause);
        mNextTrack = (ImageButton) rootView.findViewById(R.id.next_imageButton);

        // Grab calling intent, so we can take it's data! Muahahaha!
        Intent intent = getActivity().getIntent();
        // check if intent has the data we are expecting.
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String playerJsonStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.v(LOG_TAG, playerJsonStr);
            setTracks(playerJsonStr);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Let's setup listeners for our play,pause,next,previous buttons.
        mPlayPauseTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer.isPlaying()) {
                    mPlayPauseTrack.setImageDrawable(null);
                    mPlayPauseTrack.setImageDrawable(
                            CompatibilityImageFunctions.getDrawable(getActivity(), android.R.drawable.ic_media_play, getResources())
                    );
                    mMediaPlayer.pause();
                }
                else {
                    mPlayPauseTrack.setImageDrawable(
                            CompatibilityImageFunctions.getDrawable(getActivity(), android.R.drawable.ic_media_pause, getResources())
                    );
                    mMediaPlayer.start();
                }
            }
        });
        mNextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure we are not at the last track in the list.
                if(mCurrentTrackPosition == (mTracks.size() - 1)) {
                    DisplayFunctions.shortToast(getActivity(), "You are at the last track.");
                    return;
                }
                mCurrentTrackPosition++;
                mCurrentTrack = mTracks.get(mCurrentTrackPosition);
                setViewToCurrentTrack();
                playCurrentTrack();
            }
        });
        mPreviousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure we are not at the last track in the list.
                if(mCurrentTrackPosition == 0) {
                    DisplayFunctions.shortToast(getActivity(), "You are at the first track.");
                    return;
                }
                mCurrentTrackPosition--;
                mCurrentTrack = mTracks.get(mCurrentTrackPosition);
                setViewToCurrentTrack();
                playCurrentTrack();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        releaseMediaPlayer();
        mMediaPlayer = new MediaPlayer();
        // We will only be streaming music.
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        setViewToCurrentTrack();
        // Let's go ahead and play the selected track for the user! That's what they want, anyhow.
        playCurrentTrack();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    private void setViewToCurrentTrack() {
        mEntityNames.setText(mCurrentTrack.getEntityNames());
        mAlbumName.setText(mCurrentTrack.getAlbumName());
        mTrackName.setText(mCurrentTrack.getTrackName());
        // TODO mSeekBar.
        // Call update image code.
        Picasso.with(getActivity())
                .load(mCurrentTrack.getImageUrl())
                .into(mAlbumArt);
    }

    /**
     * Sets local list of track vos and sets the current track.
     *
     * @param playerJson
     */
    private void setTracks(String playerJson) {
        try {
            JSONObject playerObj = new JSONObject(playerJson);
            mEntityId = playerObj.getString(PlayerJson.ENTITY_ID);
            JSONArray tracksArr = playerObj.getJSONArray(PlayerJson.TRACKS);
            int currentTrackIdx;
            for (currentTrackIdx = 0; currentTrackIdx < tracksArr.length(); currentTrackIdx++) {
                //Extract each track info and place in trackVo.
                TrackVo tVo = new TrackVo();
                JSONObject currentTrack = tracksArr.getJSONObject(currentTrackIdx);
                tVo.setEntityNames(currentTrack.getString(PlayerJson.ENTITY_NAMES));
                tVo.setAlbumName(currentTrack.getString(PlayerJson.ALBUM_NAME));
                tVo.setImageUrl(currentTrack.getString(PlayerJson.IMAGE_URL));
                tVo.setTrackName(currentTrack.getString(PlayerJson.TRACK_NAME));
                tVo.setPreviewUrl(currentTrack.getString(PlayerJson.PREVIEW_URL));
                tVo.setDuration(currentTrack.getLong((PlayerJson.DURATION)));
                // Set the selected track of the class.
                if (currentTrack.getBoolean(PlayerJson.SELECTED) == true) {
                    mCurrentTrackPosition = currentTrackIdx;
                    mCurrentTrack = tVo;
                }
                // Finally, add to master list.
                mTracks.add(tVo);
            }
        } catch (JSONException je) {
            Log.e(LOG_TAG, "ERROR", je);
        }

    }

    private void playCurrentTrack() {
        // Don't do anything if there's no network.
        if (!NetworkFunctions.isNetworkAvailable(getActivity())) {
            DisplayFunctions.shortToast(getActivity(), "No network available!");
            return;
        }
        stopMediaPlayer();
        long startTime = System.currentTimeMillis();
        try {
            mMediaPlayer.setDataSource(mCurrentTrack.getPreviewUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException ie) {
            Log.e(LOG_TAG, "ERROR", ie);
        }
        Log.v(LOG_TAG, "all functions took " + (System.currentTimeMillis() - startTime) + "!");
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

    private void stopMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }
}

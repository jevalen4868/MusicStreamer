package com.fallenman.apps.musicstreamer;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
public class PlayerActivityFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private static MediaPlayer mMediaPlayer;
    // All variables regarding current track playing.
    private ScrollView mScrollView;
    private String mEntityId;
    private TextView mEntityNames;
    private TextView mTrackName;
    private TextView mAlbumName;
    private ResizableImageView mAlbumArt;
    private SeekBar mSeekBar;
    private TextView mTrackMax;
    private TextView mTrackProgress;
    private ImageButton mPlayPauseTrack;
    private ImageButton mNextTrack;
    private ImageButton mPreviousTrack;
    // A list which contains all the mTracks pass in.
    private List<TrackVo> mTracks = new ArrayList<TrackVo>();

    // Variables for tracking the current track.
    private TrackVo mCurrentTrack;
    private int mCurrentTrackPosition;
    private int mCurrentTrackDuration;
    private boolean mPaused = false;

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
        mTrackProgress = (TextView) rootView.findViewById(R.id.track_progress_textView);
        mTrackMax = (TextView) rootView.findViewById(R.id.track_max_textView);
        mPreviousTrack = (ImageButton) rootView.findViewById(R.id.previous_imageButton);
        mPlayPauseTrack = (ImageButton) rootView.findViewById(R.id.play_pause_imageButton);
        mPlayPauseTrack.setImageResource(android.R.drawable.ic_media_pause);
        mNextTrack = (ImageButton) rootView.findViewById(R.id.next_imageButton);

        // Now grab arguments from calling intent.
        if(savedInstanceState == null) {
            String playerJsonStr = "";
            Bundle args = getArguments(); // We were started as a dialog.
            if (args != null) {
                playerJsonStr = args.getString(PlayerJson.PLAYER_JSON_BUNDLE_ID);
            }
            Log.v(LOG_TAG, playerJsonStr);
            setTracks(playerJsonStr);

            mediaPlayerInit();
            prepareCurrentTrack();
            setViewToCurrentTrack();
            //Make sure you update Seekbar on UI thread
            final Handler mHandler = new Handler();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null) {
                        mCurrentTrackDuration = mMediaPlayer.getCurrentPosition();
                        int durationInSecs = mCurrentTrackDuration / 1000;
                        mSeekBar.setProgress(durationInSecs);
                        // now update the number progress. I'm sorry I cheated in sake of getting the app done :(
                        String durationFormatted;
                        if(durationInSecs < 10) {
                            durationFormatted = "0" + durationInSecs;
                        }
                        else {
                            durationFormatted = String.valueOf(durationInSecs);
                        }
                        mTrackProgress.setText("00:" + durationFormatted);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });
        }
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Let's setup listeners for our play,pause,next,previous buttons.
        mPlayPauseTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    mPlayPauseTrack.setImageDrawable(
                            CompatibilityImageFunctions.getDrawable(getActivity(), android.R.drawable.ic_media_play, getResources())
                    );
                    mMediaPlayer.pause();
                    mPaused = true;
                    mCurrentTrackDuration = mMediaPlayer.getCurrentPosition();
                } else { // Stopped, resume playback or begin new track.
                    mPlayPauseTrack.setImageDrawable(
                            CompatibilityImageFunctions.getDrawable(getActivity(), android.R.drawable.ic_media_pause, getResources())
                    );
                    mPaused = false;
                    // handle if user seeked while paused.
                    if (mCurrentTrackDuration > 0) {
                        mMediaPlayer.seekTo(mCurrentTrackDuration);
                        mMediaPlayer.start();
                    } else // new track selected, as we reset track duration when a new track is selected
                    {
                        prepareCurrentTrack();
                    }
                }
            }
        });
        mNextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure we are not at the last track in the list.
                if (mCurrentTrackPosition == (mTracks.size() - 1)) {
                    DisplayFunctions.shortToast(getActivity(), "You are at the last track.");
                    return;
                }
                mCurrentTrackPosition++;
                mCurrentTrack = mTracks.get(mCurrentTrackPosition);
                setViewToCurrentTrack();
                prepareCurrentTrack();
            }
        });
        mPreviousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure we are not at the last track in the list.
                if (mCurrentTrackPosition == 0) {
                    DisplayFunctions.shortToast(getActivity(), "You are at the first track.");
                    return;
                }
                mCurrentTrackPosition--;
                mCurrentTrack = mTracks.get(mCurrentTrackPosition);
                setViewToCurrentTrack();
                // Only play the track if the user is already playing music.
                prepareCurrentTrack();
            }
        });
        // Now for the seek bar.
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int selectedDuration = seekBar.getProgress();
                // If the user didn't select a new position, return.
                if (selectedDuration == mCurrentTrackDuration) {
                    return;
                }
                // Set this so that when playing is resumed, it'll seek to the right place.
                mCurrentTrackDuration = selectedDuration * 1000;
                // If playing.
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(mCurrentTrackDuration);
                    mMediaPlayer.start();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // If paused go ahead and release the activity.
        if( mPaused ) {
            try {
                mTracks = null;
                releaseMediaPlayer();
                mMediaPlayer = null;
            } catch (IllegalStateException ise) {
                Log.e(LOG_TAG, "ERROR", ise);
                mMediaPlayer = null;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp != null && ! mPaused) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // If the current track is done.
        if( ! mPaused  && mNextTrack != null) {
            mNextTrack.performClick();
        }
    }

    private void mediaPlayerInit() {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            // We will only be streaming music.
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        }
    }

    private void setViewToCurrentTrack() {
        mEntityNames.setText(mCurrentTrack.getEntityNames());
        mAlbumName.setText(mCurrentTrack.getAlbumName());
        mTrackName.setText(mCurrentTrack.getTrackName());
        mCurrentTrackDuration = 0;
        mTrackMax.setText("00:30");
        mSeekBar.setProgress(0);
        mSeekBar.setMax(30);
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

    private void prepareCurrentTrack() {
        // Our activity could be null here, let's check.
        if(getActivity() == null) {
            return;
        }
        // Don't do anything if there's no network.
        if (!NetworkFunctions.isNetworkAvailable(getActivity())) {
            DisplayFunctions.shortToast(getActivity(), "No network available!");
            return;
        }
        resetMediaPlayer();
        try {
            // make sure we are always at the pause button when track starts, if not paused.
            if( ! mPaused ) {
                mPlayPauseTrack.setImageDrawable(
                        CompatibilityImageFunctions.getDrawable(getActivity(), android.R.drawable.ic_media_pause, getResources()));
            }
            mMediaPlayer.setDataSource(mCurrentTrack.getPreviewUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException ie) {
            Log.e(LOG_TAG, "ERROR", ie);
        }
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
        }
    }

    private void resetMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        } else if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }
}

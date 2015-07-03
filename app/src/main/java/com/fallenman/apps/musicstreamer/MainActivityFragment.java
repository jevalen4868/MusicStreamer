package com.fallenman.apps.musicstreamer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.connector.MusicConnector;
import com.fallenman.apps.musicstreamer.connector.MusicVO;
import com.fallenman.apps.musicstreamer.factory.MusicFactory;
import com.fallenman.apps.musicstreamer.R;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private EditText artistName;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        // Instead of using anonymous inner class, I prefer to define it.
        ArtistNameListener anl = new ArtistNameListener();
        artistName = (EditText) getActivity().findViewById(R.id.editText_artist);
        artistName.setOnEditorActionListener(anl);
        super.onStart();
    }

    // PLEASE DEFINE SUB CLASSES BELOW THIS COMMENT.////////////////////////////////////////////////

    static class ViewHolder {
        TextView entityName;
        ImageView entityImage;
        int position;
    }

    public class ArtistNameListener implements TextView.OnEditorActionListener
    {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            // Use input method manager to close the keyboard on enter.
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    artistName.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            // Now request data from MusicConnector.
            CharSequence entityQueryName = textView.getText();
            MusicConnector connector = MusicFactory.getConnector();
            List<MusicVO> musicVoList = connector.getMusicVoList(entityQueryName.toString());
            for(MusicVO mVo : musicVoList)
            {
                Log.v(LOG_TAG, mVo.getEntityName() + " " + mVo.getEntityImageUrl());
            }
            return true;
        }
    }
}

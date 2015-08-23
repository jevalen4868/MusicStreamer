package com.fallenman.apps.musicstreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.R;
import com.fallenman.apps.musicstreamer.vo.TrackVo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jeremyvalenzuela on 7/7/15.
 */
public class TrackAdapter extends ArrayAdapter<TrackVo> {

    private static final String LOG_TAG = TrackAdapter.class.getSimpleName();

    public TrackAdapter(Context context, int resource, List<TrackVo> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        TrackViewHolder tvh = new TrackViewHolder();
        if (rowView == null) {
            // Layout hasn't been inflated.
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.track_layout, parent, false);
            // Construct view holder to save on valuable processor cycles!
            tvh.trackData = (TextView) rowView.findViewById(R.id.textView_trackData);
            tvh.albumImage = (ImageView) rowView.findViewById(R.id.imageView_albumImage);
            rowView.setTag(tvh);
        } else // our data is cached!
        {
            tvh = (TrackViewHolder) rowView.getTag();
        }
        // Set data to our views!
        TrackVo tVo = getItem(position);
        if (tVo != null) {
            tvh.entityNames = tVo.getEntityNames();
            tvh.trackData.setText(tVo.getTrackName() + "\n" + tVo.getAlbumName());
            tvh.previewUrl = tVo.getPreviewUrl();
            // Now attempt to fetch the image via Picasso.
            String imageUrl = tVo.getImageUrl();
            Picasso.with(getContext())
                    .load(imageUrl)
                    .resize(100, 100)
                    .into(tvh.albumImage);
        }
        return rowView;
    }

    protected static class TrackViewHolder {
        String entityNames;
        String previewUrl;
        TextView trackData;
        ImageView albumImage;
    }
}

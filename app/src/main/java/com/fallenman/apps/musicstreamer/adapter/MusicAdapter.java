package com.fallenman.apps.musicstreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.R;
import com.fallenman.apps.musicstreamer.vo.EntityVo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 */
public class MusicAdapter extends ArrayAdapter<EntityVo> {

    private static final String LOG_TAG = MusicAdapter.class.getSimpleName();

    public MusicAdapter(Context context, int resource, List<EntityVo> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        EntityViewHolder evh = new EntityViewHolder();
        if(rowView == null) {
            // Layout hasn't been inflated.
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.layout_main, parent, false);
            // Construct view holder to save on valuable processor cycles!
            evh.entityName = (TextView)rowView.findViewById(R.id.textView_entityName);
            evh.entityImage= (ImageView)rowView.findViewById(R.id.imageView_entityImage);
            rowView.setTag(evh);
        }
        else // our data is cached!
        {
            evh = (EntityViewHolder) rowView.getTag();
        }
        // Set data to our views!
        EntityVo eVo = getItem(position);
        if(eVo != null) {
            evh.entityId = eVo.getId();
            evh.entityName.setText(eVo.getEntityName());
            // Now attempt to fetch the image via Picasso.
            String imageUrl = eVo.getEntityImageUrl();
            Picasso.with(getContext())
                    .load(imageUrl)
                    .resize(100, 100)
                    .into(evh.entityImage);
        }
        return rowView;
    }

    protected static class EntityViewHolder {
        String entityId;
        TextView entityName;
        ImageView entityImage;
    }
}

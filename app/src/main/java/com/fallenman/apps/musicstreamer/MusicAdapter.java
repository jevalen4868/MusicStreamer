package com.fallenman.apps.musicstreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenman.apps.musicstreamer.connector.MusicVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremyvalenzuela on 7/3/15.
 */
public class MusicAdapter extends ArrayAdapter<MusicVo> {

    public MusicAdapter(Context context, int resource, List<MusicVo> list) {
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
            evh.entityImage= (ImageView)rowView.findViewById(R.id.imageButton_entityImage);
            rowView.setTag(evh);
        }
        else // our data is cached!
        {
            evh = (EntityViewHolder) rowView.getTag();
        }
        // Set data to our views!
        MusicVo mVo = getItem(position);
        if(mVo != null) {
            evh.entityName.setText(mVo.getEntityName());
            evh.entityImage.setImageBitmap(mVo.getEntityImage());
        }
        return rowView;
    }

    protected static class EntityViewHolder {
        TextView entityName;
        ImageView entityImage;
        int position;
    }
}

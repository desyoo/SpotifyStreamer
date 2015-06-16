package com.example.desy.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.desy.spotifystreamer.R;
import com.example.desy.spotifystreamer.model.SimpleTrack;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by desy on 6/12/15.
 */
public class TopTracksAdapter extends ArrayAdapter<SimpleTrack>{

    public TopTracksAdapter(Context context, int resource, List<SimpleTrack> objects) {
        super(context, resource, objects);
    }

    public static class ViewHolder {
        ImageView ivThumbnail;
        TextView tvAblum;
        TextView tvTrack;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleTrack track = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.list_item_top_tracks,parent,false);
            viewHolder.ivThumbnail = (ImageView) convertView.findViewById(R.id.list_item_iv_thumbnail);
            viewHolder.tvAblum = (TextView) convertView.findViewById(R.id.list_item_album);
            viewHolder.tvTrack = (TextView) convertView.findViewById(R.id.list_item_track);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvAblum.setText(track.getAlbum());
        viewHolder.tvTrack.setText(track.getName());

        if (viewHolder.ivThumbnail != null ) {
            viewHolder.ivThumbnail.setImageResource(android.R.color.transparent);
            viewHolder.ivThumbnail.setImageBitmap(null);
            Picasso.with(getContext()).load(track.getThumbnail()).resize(200, 200).into(viewHolder.ivThumbnail);
        }


        return convertView;
    }
}

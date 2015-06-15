package com.example.desy.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SearchArtistAdapter extends ArrayAdapter<SimpleArtist> {

    public SearchArtistAdapter(Context context, int resource, ArrayList<SimpleArtist> objects) {
        super(context, resource, objects);
    }

    public static class ViewHolder {
        ImageView iconView;
        TextView artist;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleArtist artistsPager = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_artist,parent,false);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.list_item_artist_textview);
            viewHolder.iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.artist.setText(artistsPager.name);
//        if (viewHolder.iconView != null && getItem(position).images.size() != 0) {
//            viewHolder.iconView.setImageResource(android.R.color.transparent);
//            viewHolder.iconView.setImageBitmap(null);
//            Picasso.with(getContext()).load(artistsPager.images.get(0).url).resize(200,200).into(viewHolder.iconView);
//        }
        if (viewHolder.iconView != null ) {
            viewHolder.iconView.setImageResource(android.R.color.transparent);
            viewHolder.iconView.setImageBitmap(null);
            Picasso.with(getContext()).load(artistsPager.image_url).resize(200,200).into(viewHolder.iconView);
        }

        return convertView;
    }



}

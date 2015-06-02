package com.example.desy.spotifystreamer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;


public class SearchArtistAdapter extends ArrayAdapter<Objects> {


    public SearchArtistAdapter(Context context, List<Objects> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public class PhotoViewHolder{
        public ImageView imgProfilePicture;
        public TextView txtUserName;
        public  TextView txtCreatedTime;
        public  ImageView imgCreatedImage;
        public  TextView txtCaption;
        public  TextView txtLikes;
        public  TextView txtComments;
    }
}

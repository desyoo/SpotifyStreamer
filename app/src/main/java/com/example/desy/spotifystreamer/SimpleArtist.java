package com.example.desy.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by desy on 6/10/15.
 */
public class SimpleArtist  implements Parcelable{
    public String name;
    public String url;

    public SimpleArtist(Artist artist) {
        name = artist.name;
        url = artist.id;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}

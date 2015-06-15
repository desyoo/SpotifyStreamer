package com.example.desy.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by desy on 6/10/15.
 */
public class SimpleArtist  implements Parcelable{
    public String id;
    public String name;
    public String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


    public SimpleArtist (Artist artist) {
        id = artist.id;
        name = artist.name;
        if (artist.images.size() != 0) {
            image_url = artist.images.get(0).url;
        }
    }

    public SimpleArtist(Parcel input) {
        name = input.readString();
        image_url = input.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(image_url);
    }

    public static final Creator<SimpleArtist> CREATOR = new Creator<SimpleArtist>() {
        public SimpleArtist createFromParcel(Parcel source) {
            return new SimpleArtist(source);
        }

        public SimpleArtist[] newArray(int size) {
            return new SimpleArtist[size];
        }
    };
}

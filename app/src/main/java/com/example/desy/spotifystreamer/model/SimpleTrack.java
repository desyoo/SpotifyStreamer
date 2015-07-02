package com.example.desy.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by desy on 6/15/15.
 */
public class SimpleTrack implements Parcelable {
    String thumbnail;
    String album;
    String name;
    String trackName;
    Long trackDuration;
    String musicUrl;

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public Long getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(Long trackDuration) {
        this.trackDuration = trackDuration;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleTrack (Track track) {
        album = track.album.name;
        name = track.artists.get(0).name;
        if (track.album.images.size() != 0) {
            thumbnail = track.album.images.get(0).url;
        }
        trackName = track.name;
        trackDuration = track.duration_ms;
        //musicUrl = track.album.external_urls.get("spotify");
        musicUrl = track.preview_url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbnail);
        dest.writeString(this.album);
        dest.writeString(this.name);
        dest.writeString(this.trackName);
        dest.writeLong(this.trackDuration);
        dest.writeString(this.musicUrl);
    }

    public SimpleTrack(Parcel in) {
        this.thumbnail = in.readString();
        this.album = in.readString();
        this.name = in.readString();
        this.trackName = in.readString();
        this.trackDuration = in.readLong();
        this.musicUrl = in.readString();
    }

    public static final Creator<SimpleTrack> CREATOR = new Creator<SimpleTrack>() {
        public SimpleTrack createFromParcel(Parcel source) {
            return new SimpleTrack(source);
        }

        public SimpleTrack[] newArray(int size) {
            return new SimpleTrack[size];
        }
    };
}

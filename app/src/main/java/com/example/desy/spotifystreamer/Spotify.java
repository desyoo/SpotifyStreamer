package com.example.desy.spotifystreamer;

import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by desy on 6/2/15.
 */
public class Spotify {

    public Spotify() {
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken("a991ba8678934f28942a88840c68388a");
        SpotifyService spotify = api.getService();
        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                Log.d("Album success", album.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album failure", error.toString());
            }
        });
    }

}

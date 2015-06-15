package com.example.desy.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {
    private String LOG_TAG = TopTracksFragment.class.getSimpleName();
    private static final String STATE_TRACK = "state_track";
    private String artistID;
    private ListView mListView;
    private ArrayList<SimpleTrack> listTack = new ArrayList<>();
    private TopTracksAdapter mTopTrackAdapte;

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mListView = (ListView) rootView.findViewById(R.id.lvTopTracks);
        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if(extras == null) {
                Toast.makeText(getActivity(), "No Tracks", Toast.LENGTH_SHORT).show();
                artistID= null;
            } else {
                artistID= extras.getString("artist_id");
            }
        }
        Log.d(LOG_TAG, artistID);
        openTopTrack(artistID);

            return rootView;
        }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_TRACK, listTack);
    }


    void openTopTrack(String artist_id) {
        SpotifyApi spotifyApi = new SpotifyApi();
        Map<String, Object> options = new HashMap<>();
        options.put("country", "US");
        spotifyApi.getService().getArtistTopTrack(artist_id, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                for (Track track : tracks.tracks) {
                    listTack.add(new SimpleTrack(track));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTopTrackAdapte = new TopTracksAdapter(getActivity(), 0, listTack);
                        mListView.setAdapter(mTopTrackAdapte);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                final RetrofitError getError = error;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

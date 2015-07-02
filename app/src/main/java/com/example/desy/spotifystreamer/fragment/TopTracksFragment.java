package com.example.desy.spotifystreamer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.desy.spotifystreamer.MediaPlayActivity;
import com.example.desy.spotifystreamer.R;
import com.example.desy.spotifystreamer.adapter.TopTracksAdapter;
import com.example.desy.spotifystreamer.model.SimpleTrack;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mListView = (ListView) rootView.findViewById(R.id.lvTopTracks);
        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if(extras == null) {
                Toast.makeText(getActivity(), "No Tracks found", Toast.LENGTH_SHORT).show();
            } else {
                artistID= extras.getString("artist_id");
            }
        } else {
            listTack = savedInstanceState.getParcelableArrayList(STATE_TRACK);
            // Create an adapter
            mTopTrackAdapte = new TopTracksAdapter(getActivity(), 0, listTack);
            // Use the adapter so the listTrack can be displayed on screen :)
            mListView.setAdapter(mTopTrackAdapte);
        }

        openTopTrack(artistID);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SimpleTrack simpleTrack = listTack.get(i);
                Intent intent = new Intent(getActivity(), MediaPlayActivity.class);
                intent.putExtra("url",simpleTrack.getMusicUrl());
                intent.putExtra("thumbnail", simpleTrack.getThumbnail());
                intent.putExtra("album", simpleTrack.getAlbum());
                intent.putExtra("artistName",simpleTrack.getName());
                intent.putExtra("trackName",simpleTrack.getTrackName());
                intent.putExtra("trackDuration",simpleTrack.getTrackDuration());
                startActivity(intent);

            }
        });

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
                if (error != null) {
                    final RetrofitError getError = error;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

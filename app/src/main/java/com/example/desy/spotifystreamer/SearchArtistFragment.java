package com.example.desy.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchArtistFragment extends Fragment {
    private static final String LOG_TAG = SearchArtistFragment.class.getSimpleName();
    private static final String STATE_ARTIST = "state_artist";
    private EditText etSearch;
    private SearchArtistAdapter mSearchArtistAdapter;
    private ArrayList<SimpleArtist> list = new ArrayList<>();
    private ListView mListView;

    public SearchArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_ARTIST, list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        etSearch = ((EditText) rootView.findViewById(R.id.etSearch));
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    //Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    if (!etSearch.getText().toString().isEmpty()) {
                        searchSpotifyArtists(etSearch.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SimpleArtist artist = list.get(i);
                String id = artist.id;
                Log.d("artist_id",id);
                Intent intent = new Intent(getActivity(),TopTracks.class);
                //based on item add info to intent
                intent.putExtra("artist_id",id);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList(STATE_ARTIST);
        }

        return rootView;

    }





    private void searchSpotifyArtists(final String query){
        SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();
        // Custom method
        setLoading(true);
        spotify.searchArtists(query, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                //list = new ArrayList<>();
                if (artistsPager.artists.items.size() == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Search result not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    for (Artist artist : artistsPager.artists.items) {
                        list.add(new SimpleArtist(artist));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSearchArtistAdapter = new SearchArtistAdapter(getActivity(), 0, list);
                            mListView.setAdapter(mSearchArtistAdapter);
                        }
                    });
                }

            }

            @Override
            public void failure(RetrofitError error) {
                //Display an error message
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

    public void setLoading(boolean n) {
        if (n) {
            list.clear();
        }
        return;
    }



}

package com.example.desy.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

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
    private EditText etSearch;
    private SearchArtistAdapter mSearchArtistAdapter;
    private ArrayList<Artist> list;


    private ListView mListView;

    public SearchArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    searchSpotifyArtists(etSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mListView = (ListView) rootView.findViewById(R.id.listview_artist);
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
                list = new ArrayList<>();
                for (Artist artist : artistsPager.artists.items) {
                    list.add(artist);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSearchArtistAdapter = new SearchArtistAdapter(getActivity(), 0, list);
                        mListView.setAdapter(mSearchArtistAdapter);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                //Display an error message
            }
        });

    }

    public void setLoading(boolean n) {
        return;
    }



}

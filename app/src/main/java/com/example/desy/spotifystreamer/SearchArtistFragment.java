package com.example.desy.spotifystreamer;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchArtistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchArtistFragment extends Fragment {
    private static final String LOG_TAG = SearchArtistFragment.class.getSimpleName();
    private SearchArtistFragment mSearchArtistFragment;

    private ArrayAdapter<String> mArtistAdapter;
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
        //mSearchArtist = new SearchArtist(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //mListView = (ListView) rootView.findViewById(R.id.listview_artist);
        String[] fakedata = {"sunshine","hello","world","hello_me","hello_you"};
        ArrayList<String> testString = new ArrayList<>(Arrays.asList(fakedata));
        mArtistAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_artist,R.id.list_item_artist_textview, testString);
        return rootView;

    }


}

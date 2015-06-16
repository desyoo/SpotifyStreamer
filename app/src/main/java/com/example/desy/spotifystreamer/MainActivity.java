package com.example.desy.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.desy.spotifystreamer.adapter.SearchArtistAdapter;
import com.example.desy.spotifystreamer.fragment.SearchArtistFragment;


public class MainActivity extends ActionBarActivity {
    private SearchArtistAdapter mSearchArtistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SearchArtist searchArtist = ((SearchArtist) getSupportFragmentManager().findFragmentById(R.id.fragment_artist));
        SearchArtistFragment searchArtistFragment = ((SearchArtistFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_artist));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

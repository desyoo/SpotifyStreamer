package com.example.desy.spotifystreamer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.desy.spotifystreamer.MyMusicService;
import com.example.desy.spotifystreamer.R;
import com.example.desy.spotifystreamer.model.SimpleTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class MediaPlayActivityFragment extends Fragment implements View.OnClickListener{

    private static final String FORMAT = "%01d:%02d";

    //Intent playbackServiceIntent;
    private ImageView ivThumbnail;
    private TextView tvAlbum;
    private TextView tvName;
    private TextView tvTrackName;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private Button btBack;
    private Button btPlay;
    private Button btPause;
    private Button btForward;
    private long second;
    private long minute;
    private String simpleTime;
    private String thumbnail;
    private String album;
    private String artistName;
    private String trackName;
    private SeekBar musicSeekBar;
    private Handler durationHandler = new Handler();
    private String musicUrl;
    private Timer mTimer;
    private double timeElapsed = 0, finalTime = 0;
    boolean isPlaying = false;
    private int pos;
    private ArrayList<SimpleTrack> listTack = new ArrayList<>();
    GestureDetector gestureDetector;
    static int click = 0;

    public MediaPlayActivityFragment() {
    }

    private void initializeSettings(View rootView) {
        ivThumbnail = (ImageView) rootView.findViewById(R.id.ivAlbumArtwork);
        tvAlbum = (TextView) rootView.findViewById(R.id.tvAlbumName);
        tvName = (TextView) rootView.findViewById(R.id.tvArtistName);
        tvTrackName = (TextView) rootView.findViewById(R.id.tvTrackName);
        tvStartTime = (TextView) rootView.findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) rootView.findViewById(R.id.tvEndtime);
        btBack = (Button) rootView.findViewById(R.id.btBack);
        btPlay = (Button) rootView.findViewById(R.id.btPlay);
        btPause = (Button) rootView.findViewById(R.id.btPause);
        btForward = (Button) rootView.findViewById(R.id.btForward);
        musicSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        musicSeekBar.setMax((int) finalTime);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_play, container, false);

        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            second = (extras.getLong("trackDuration") / 1000) % 60;
            minute = (extras.getLong("trackDuration") / (1000 * 60)) % 60;
            //long hour = (extras.getLong("trackDuration") / (1000 * 60 * 60)) % 24;
            simpleTime = String.format(FORMAT, minute,second);
            thumbnail = extras.getString("thumbnail");
            album = extras.getString("album");
            artistName = extras.getString("artistName");
            trackName = extras.getString("trackName");
            musicUrl = extras.getString("url");
            pos = extras.getInt("position");
            listTack = extras.getParcelableArrayList("listTopTrack");
        }

        Log.d("music_url", musicUrl);
        initializeSettings(rootView);

        Picasso.with(getActivity()).load(thumbnail).into(ivThumbnail);
        tvEndTime.setText(simpleTime);
        tvAlbum.setText(album);
        tvName.setText(artistName);
        tvTrackName.setText(trackName);

        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btForward.setOnClickListener(this);


        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //seekTo(progress);
                    int seekPos = seekBar.getProgress();
                    Intent intent = new Intent(getActivity(), MyMusicService.class);
                    intent.putExtra("pos", seekPos);
                    intent.setAction(MyMusicService.ACTION_SEEK);
                    getActivity().startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }



//handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            //timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            musicSeekBar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            tvStartTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onClick(View view) {
        // Send the correct intent to the MusicService, according to the button that was clicked
        if (view == btPlay) {
            Intent i = new Intent(getActivity(), MyMusicService.class);
            i.setAction(MyMusicService.ACTION_PLAY);
            i.putExtra("url", musicUrl);
            getActivity().startService(i);
            btPlay.setVisibility(View.INVISIBLE);
            btPause.setVisibility(View.VISIBLE);
            btPlay.setEnabled(false);
            btPause.setEnabled(true);
            Log.d("music","music_is_on");
        } else if (view == btPause) {
            Log.d("music", "music_is_off");
            Intent i = new Intent(getActivity(), MyMusicService.class);
            i.setAction(MyMusicService.ACTION_PAUSE);
            getActivity().startService(i);
            btPlay.setVisibility(View.VISIBLE);
            btPause.setVisibility(View.INVISIBLE);
            btPlay.setEnabled(true);
            btPause.setEnabled(false);
        }
        else if (view == btForward) {
            pos++;
            if (pos >= listTack.size()) {
                pos = 0;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getActivity()).load(listTack.get(pos).getThumbnail()).into(ivThumbnail);
                    //tvEndTime.setText(simpleTime);
                    tvAlbum.setText(listTack.get(pos).getAlbum());
                    tvName.setText(listTack.get(pos).getName());
                    tvTrackName.setText(listTack.get(pos).getTrackName());
                    btPlay.setVisibility(View.INVISIBLE);
                    btPause.setVisibility(View.VISIBLE);
                    btPlay.setEnabled(false);
                    btPause.setEnabled(true);
                }
            });
            Intent i = new Intent(getActivity(), MyMusicService.class);
            i.putExtra("url", listTack.get(pos).getMusicUrl());
            i.setAction(MyMusicService.ACTION_PLAY);
            getActivity().startService(i);
        }
        else if (view == btBack) {
            click++;
            Handler handler = new Handler();
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    // single click *******************************
                    if (click == 1) {
                        Intent i = new Intent(getActivity(), MyMusicService.class);
                        i.setAction(MyMusicService.ACTION_REWIND);
                        getActivity().startService(i);
                    }
                    // double click *********************************
                    if (click == 2) {
                        pos--;
                        if (pos < 0) {
                            pos = listTack.size()-1;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(getActivity()).load(listTack.get(pos).getThumbnail()).into(ivThumbnail);
                                //tvEndTime.setText(simpleTime);
                                tvAlbum.setText(listTack.get(pos).getAlbum());
                                tvName.setText(listTack.get(pos).getName());
                                tvTrackName.setText(listTack.get(pos).getTrackName());
                                btPlay.setVisibility(View.INVISIBLE);
                                btPause.setVisibility(View.VISIBLE);
                                btPlay.setEnabled(false);
                                btPause.setEnabled(true);
                            }
                        });
                        Intent i = new Intent(getActivity(), MyMusicService.class);
                        i.putExtra("url", listTack.get(pos).getMusicUrl());
                        i.setAction(MyMusicService.ACTION_PLAY);
                        getActivity().startService(i);
                    }
                    click = 0;
                }
            };
            if (click == 1) {
                handler.postDelayed(r, 500);
            }

        }
    }




    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

            return true;
        }
    }

}

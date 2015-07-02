package com.example.desy.spotifystreamer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.desy.spotifystreamer.MyMusicService;
import com.example.desy.spotifystreamer.R;
import com.squareup.picasso.Picasso;

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
    private MyMusicService myMusicService;



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
        btForward = (Button) rootView.findViewById(R.id.btForward);
        musicSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        musicSeekBar.setMax((int)finalTime);
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
        }
        Log.d("music_url",musicUrl);
        initializeSettings(rootView);

        Picasso.with(getActivity()).load(thumbnail).into(ivThumbnail);
        tvEndTime.setText(simpleTime);
        tvAlbum.setText(album);
        tvName.setText(artistName);
        tvTrackName.setText(trackName);


        //playbackServiceIntent = new Intent(getActivity(), MyMusicService.class);

//        btPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
                //startService(playbackServiceIntent);
//                if (mPlayer.isPlaying()) {
//                    mPlayer.pause();
//                    btnPlay.setImageResource(R.drawable.bg_selector_btn_play);
//                } else {
//                    mPlayer.start();
//                    btnPlay.setImageResource(R.drawable.bg_selector_btn_pauce);
//                }
//                Intent intent = new Intent(getActivity(), MyMusicService.class);
//                intent.putExtra("KEY1", musicUrl);
//                getActivity().startService(intent);
//            }
//        });

        btPlay.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btForward.setOnClickListener(this);

//        Intent i = new Intent(MyMusicService.ACTION_URL);
//        Uri uri = Uri.parse(musicUrl);
//        i.setData(uri);


        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //seekTo(progress);
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
            Intent i = new Intent(MyMusicService.ACTION_PLAY);
            Uri uri = Uri.parse(musicUrl);
            i.setData(uri);
            getActivity().startService(i);

            //getActivity().startService(new Intent(MyMusicService.ACTION_PLAY));
        }
//        else if (view == mPauseButton)
//            startService(new Intent(MusicService.ACTION_PAUSE));
        else if (view == btForward)
            getActivity().startService(new Intent(MyMusicService.ACTION_SKIP));
        else if (view == btBack)
            getActivity().startService(new Intent(MyMusicService.ACTION_REWIND));
    }
}

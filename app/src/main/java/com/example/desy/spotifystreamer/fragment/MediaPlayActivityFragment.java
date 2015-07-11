package com.example.desy.spotifystreamer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.desy.spotifystreamer.MyMusicService;
import com.example.desy.spotifystreamer.R;
import com.example.desy.spotifystreamer.Utilities;
import com.example.desy.spotifystreamer.model.SimpleTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;


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
    private Utilities utils;
    MyMusicService mService;
    Handler updateHandler;
    int timer;
    CountDownTimer countDown;

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
        //musicSeekBar.setMax((int) finalTime);
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
        utils = new Utilities();
        Log.d("music_url", musicUrl);
        initializeSettings(rootView);

        Picasso.with(getActivity()).load(thumbnail).into(ivThumbnail);
        tvEndTime.setText("0:30");
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
//                    //seekTo(progress);
//                    int seekPos = seekBar.getProgress();
//                    Intent intent = new Intent(getActivity(), MyMusicService.class);
//                    intent.putExtra("pos", seekPos);
//                    intent.setAction(MyMusicService.ACTION_SEEK);
//                    getActivity().startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
// remove message Handler from updating progress bar
                //durationHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //durationHandler.removeCallbacks(mUpdateTimeTask);
                //int totalDuration = mp.getDuration();
                //int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                //mp.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });


        new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

            }
        }.cancel();

        return rootView;
    }

    public void updateProgressBar() {
        //durationHandler.postDelayed(mUpdateTimeTask, 100);
    }


//handler to change seekBarTime
//    private Runnable updateSeekBarTime = new Runnable() {
//        public void run() {
//            //get current position
//            //timeElapsed = mediaPlayer.getCurrentPosition();
//            //set seekbar progress
//            musicSeekBar.setProgress((int) timeElapsed);
//            //set time remaing
//            double timeRemaining = finalTime - timeElapsed;
//            tvStartTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
//            //repeat yourself that again in 100 miliseconds
//            durationHandler.postDelayed(this, 100);
//        }
//    };





//    private Runnable mUpdateTimeTask = new Runnable() {
//        public void run() {
//            long totalDuration = MyMusicService.getDuration();
//            long currentDuration = mp.getCurrentPosition();
//
//            // Displaying Total Duration time
//            tvStartTime.setText(""+utils.milliSecondsToTimer(totalDuration));
//            // Displaying time completed playing
//            tvEndTime.setText("" + utils.milliSecondsToTimer(currentDuration));
//
//            // Updating progress bar
//            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
//            //Log.d("Progress", ""+progress);
//            musicSeekBar.setProgress(progress);
//
//            // Running this thread after 100 milliseconds
//            durationHandler.postDelayed(this, 100);
//        }
//    };


    @Override
    public void onClick(View view) {
        // Send the correct intent to the MusicService, according to the button that was clicked
        if (view == btPlay) {
            Intent i = new Intent(getActivity(), MyMusicService.class);
            i.setAction(MyMusicService.ACTION_PLAY);
            i.putExtra("url", musicUrl);
            getActivity().startService(i);
            countDown = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    //tvStartTime.setText("seconds remaining: " + millisUntilFinished / 1000);
                    timer++;
                    if (timer < 10) {
                        tvStartTime.setText("0:0" + timer);
                    } else {
                        tvStartTime.setText("0:" + timer);
                    }
                    musicSeekBar.setProgress(0);
                    musicSeekBar.setMax((int) (30));
                    musicSeekBar.setProgress((int) (timer));
                }
                public void onFinish() {
                    timer = 0;
                    tvStartTime.setText("0:00");
                }
            }.start();

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
            countDown.cancel();
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
            timer = 0;
            countDown = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    //tvStartTime.setText("seconds remaining: " + millisUntilFinished / 1000);
                    timer++;
                    if (timer < 10) {
                        tvStartTime.setText("0:0" + timer);
                    } else {
                        tvStartTime.setText("0:" + timer);
                    }
                    musicSeekBar.setProgress(0);
                    musicSeekBar.setMax((int) (30));
                    musicSeekBar.setProgress((int) (timer));
                }
                public void onFinish() {
                    timer = 0;
                    tvStartTime.setText("0:00");
                }
            }.start();
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
                        timer = 0;
                        countDown = new CountDownTimer(30000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                //tvStartTime.setText("seconds remaining: " + millisUntilFinished / 1000);
                                timer++;
                                if (timer < 10) {
                                    tvStartTime.setText("0:0" + timer);
                                } else {
                                    tvStartTime.setText("0:" + timer);
                                }
                                musicSeekBar.setProgress(0);
                                musicSeekBar.setMax((int) (30));
                                musicSeekBar.setProgress((int) (timer));
                            }
                            public void onFinish() {
                                timer = 0;
                                tvStartTime.setText("0:00");
                            }
                        }.start();
                    }
                    click = 0;
                }
            };
            if (click == 1) {
                handler.postDelayed(r, 500);
            }

        }
    }



}

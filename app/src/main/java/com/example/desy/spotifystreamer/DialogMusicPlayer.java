package com.example.desy.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.desy.spotifystreamer.model.SimpleTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by desy on 7/14/15.
 */
public class DialogMusicPlayer extends DialogFragment implements View.OnClickListener {
    private static final String LIST_TRACK = "listTopTrack";
    private static final String FORMAT = "%01d:%02d";
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
    private String musicUrl;
    private int pos;
    static int click = 0;
    int timer;
    CountDownTimer countDown;
    private ArrayList<SimpleTrack> listTack = new ArrayList<>();

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


    public DialogMusicPlayer() {

    }

    public static DialogMusicPlayer newInstance(String title) {
        DialogMusicPlayer frag = new DialogMusicPlayer();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_media_player, container);
        getDialog().setTitle("DialogFragment");
        Bundle arguments = getArguments();
        if (savedInstanceState == null) {
            listTack = arguments.getParcelableArrayList(LIST_TRACK);
            pos = arguments.getInt("position");
            thumbnail = listTack.get(pos).getThumbnail();
            album = listTack.get(pos).getAlbum();
            artistName = listTack.get(pos).getName();
            trackName = listTack.get(pos).getTrackName();
            musicUrl = listTack.get(pos).getMusicUrl();
        }

        initializeSettings(rootView);

        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);

        Picasso.with(getActivity()).load(thumbnail).into(ivThumbnail);
        tvEndTime.setText("0:30");
        tvAlbum.setText(album);
        tvName.setText(artistName);
        tvTrackName.setText(trackName);

        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btForward.setOnClickListener(this);
        // Do something else
        return rootView;
    }


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
            Log.d("music", "music_is_on");
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
            countDown.cancel();
            Handler handler = new Handler();
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    // single click *******************************
                    if (click == 1) {
                        Intent i = new Intent(getActivity(), MyMusicService.class);
                        i.setAction(MyMusicService.ACTION_REWIND);
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

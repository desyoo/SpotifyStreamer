package com.example.desy.spotifystreamer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.desy.spotifystreamer.model.SimpleTrack;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by desy on 6/18/15.
 */
public class MyMusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    // The tag we put on debug messages
    final static String TAG = "MyMusicService";
    // These are the Intent actions that we are prepared to handle. Notice that the fact these
    // constants exist in our class is a mere convenience: what really defines the actions our
    // service can handle are the <action> tags in the <intent-filters> tag for our service in
    // AndroidManifest.xml.
    public static final String ACTION_TOGGLE_PLAYBACK =
            "com.example.desy.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "com.example.desy.spotifystreamer.PLAY";
    public static final String ACTION_PAUSE = "com.example.desy.spotifystreamer.PAUSE";
    public static final String ACTION_STOP = "com.example.desy.spotifystreamer.STOP";
    public static final String ACTION_SKIP = "com.example.desy.spotifystreamer.SKIP";
    public static final String ACTION_REWIND = "com.example.desy.spotifystreamer.REWIND";
    public static final String ACTION_SEEK = "com.example.desy.spotifystreamer.SEEK";

    // title of the song we are currently playing
    String mSongTitle = "";

    // The ID we use for the notification (the onscreen alert that appears at the notification
    // area at the top of the screen as an icon -- and as text as well if the user expands the
    // notification area).
    final int NOTIFICATION_ID = 1;
    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;
    // our media player
    MediaPlayer mMediaPlayer = null;
    // whether the song we are playing is streaming from the network
    boolean mIsStreaming = false;
    // Wifi lock that we hold when streaming files from the internet, in order to prevent the
    // device from shutting off the Wifi radio
    WifiManager.WifiLock mWifiLock;
    AudioManager mAudioManager;
    NotificationManager mNotificationManager;
    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;

    Notification mNotification = null;
    // if in Retrieving mode, this flag indicates whether we should start playing immediately
    // when we are ready or not.
    boolean mStartPlayingAfterRetrieve = false;


    //song list
    private ArrayList<SimpleTrack> songs;
    //current position
    private int songPosn;


    // indicates the state our service:
    enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
        // paused in this state if we don't have audio focus. But we stay in this state
        // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    };
    State mState = State.Retrieving;


    public MyMusicService () {
    }

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MyMusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyMusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(ACTION_PLAY)) {
            Bundle extras = intent.getExtras();
            String url = extras.getString("url");
            playNextSong(url);
        } else if (intent.getAction().equals(ACTION_PAUSE)) {
            Toast.makeText(this, "service pause", Toast.LENGTH_SHORT).show();
            processPauseRequest();
        }
        else if (intent.getAction().equals(ACTION_REWIND)) {
            processRewindRequest();
        } else if (intent.getAction().equals(ACTION_SEEK)) {
            Bundle extras = intent.getExtras();
            int pos = extras.getInt("pos");
            updateSeekPos(pos);
        }

        return START_NOT_STICKY; // Means we started the service, but don't want it to
        // restart in case it's killed.
    }


    void playNextSong (String url) {
        if (mState == State.Paused) {
            mState = State.Playing;
            if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
            return;
        }
        relaxResources(false); // release everything except MediaPlayer
        try {
            createMediaPlayerIfNeeded();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);
            mIsStreaming = url.startsWith("http:") || url.startsWith("https:");

            //mState = State.Preparing;
            //setUpAsForeground("mSongTitle" + " (loading)");

            // starts preparing the media player in the background. When it's done, it will call
            // our OnPreparedListener (that is, the onPrepared() method on this class, since we set
            // the listener to 'this').
            // Until the media player is prepared, we *cannot* call start() on it!
            mMediaPlayer.prepareAsync();

            // If we are streaming from the internet, we want to hold a Wifi lock, which prevents
            // the Wifi radio from going to sleep while the song is playing. If, on the other hand,
            // we are *not* streaming, we want to release the lock if we were holding it before.
            if (mIsStreaming) mWifiLock.acquire();
            else if (mWifiLock.isHeld()) mWifiLock.release();


        } catch (IOException ex) {
            Log.e("MusicService", "IOException playing next song: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    void processPauseRequest() {
        mState = State.Paused;
        mMediaPlayer.pause();
        //relaxResources(false); // while paused, we always retain the MediaPlayer
    }

    void processRewindRequest() {

        //if (mState == State.Playing || mState == State.Paused) {
            Toast.makeText(this, "service rewind", Toast.LENGTH_SHORT).show();
            mMediaPlayer.seekTo(0);
        //}
    }

    void processSkipRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            playNextSong(null);
        }
    }

    /**
     * Makes sure the media player exists and has been reset. This will create the media player
     * if needed, or reset the existing media player if one already exists.
     */
    void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            // Make sure the media player will acquire a wake-lock while playing. If we don't do
            // that, the CPU might go to sleep while the song is playing, causing playback to stop.
            //
            // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
            // permission in AndroidManifest.xml.
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);

        }
        else
            mMediaPlayer.reset();
    }


    /**
     * Releases resources used by the service for playback. This includes the "foreground service"
     * status and notification, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
     */
    void relaxResources(boolean releaseMediaPlayer) {
        // stop being a foreground service
        stopForeground(true);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();
    }


    /**
     * Configures service as a foreground service. A foreground service is a service that's doing
     * something the user is actively aware of (such as playing music), and must appear to the
     * user as a notification. That's why we create the notification here.
     */
    void setUpAsForeground(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new Notification();
        mNotification.tickerText = text;
        //todo
        mNotification.icon = R.drawable.abc_ic_clear_mtrl_alpha;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.setLatestEventInfo(getApplicationContext(), "RandomMusicPlayer",
                text, pi);
        startForeground(NOTIFICATION_ID, mNotification);
    }

    /** Updates the notification. */
    void updateNotification(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MediaPlayActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.setLatestEventInfo(getApplicationContext(), "RandomMusicPlayer", text, pi);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }



    public void updateSeekPos(int seekPos) {
        if (mMediaPlayer.isPlaying()) {
            int playPositionInMillisecconds = (mMediaPlayer.getDuration() / 100)
                    * seekPos;
            mMediaPlayer.seekTo(playPositionInMillisecconds);
        }
    }

    public int getSeekPos () {
        return mMediaPlayer.getCurrentPosition();
    }

    public void setList(ArrayList<SimpleTrack> theSongs){
        songs=theSongs;
    }



    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        // The media player is done preparing. That means we can start playing!
        //mState = State.Playing;
        //updateNotification(mSongTitle + " (playing)");
        player.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // The media player finished playing the current song, so we go ahead and start the next.
        //playNextSong(null);
        relaxResources(true);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(getApplicationContext(), "Media player error! Resetting.",
                Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: what=" + String.valueOf(i) + ", extra=" + String.valueOf(i1));
        //mState = State.Retrieving;
        relaxResources(true);
        return true; // true indicates we handled the error
    }

    @Override
    public void onDestroy() {
        relaxResources(true);
        super.onDestroy();
    }
}

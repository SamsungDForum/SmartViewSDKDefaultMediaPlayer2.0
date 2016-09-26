package samsung.mediaplayerqueue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.samsung.multiscreen.VideoPlayer;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * @author Ankit Saini
 * This class manages the playback controls for videos & youtube.
 */
public class PlaybackControls
        extends Dialog
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {//,
        //PlayerNotice{
    private static final String TAG = "PlaybackControls";
    private static PlaybackControls mInstance = null;

    private Button btnPlayPause;
    private Button btnStop;
    private Button btnForward;
    private Button btnRewind;
    private Button btnMuteUnmute;
    private Button btnRepeat;
    private Button btnShuffle;
    private Button btnNext;
    private Button btnPrevious;
    private SeekBar videoSeekBar;
    private SeekBar volumeSeekBar;
    private TextView txtTitle;
    private ImageView imgThumbnail;

    private int videoProgress = 0;
    private int videoVolume = 0;

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    private Boolean isMute;
    private Boolean isPlaying;

    Activity mActivity = null;

    private PlaybackControls(Context context) {
        super(context);
    }

    public void init(Activity activity) {
        mActivity = activity;
        btnPlayPause    = (Button)activity.findViewById(R.id.btnPlay);
        btnStop         = (Button)activity.findViewById(R.id.btnStop);
        btnForward      = (Button)activity.findViewById(R.id.btnForward);
        btnRewind       = (Button)activity.findViewById(R.id.btnRewind);
        btnMuteUnmute   = (Button)activity.findViewById(R.id.btnMute);
        btnRepeat       = (Button)activity.findViewById(R.id.btnRepeat);
        btnNext         = (Button)activity.findViewById(R.id.btnNext);
        btnPrevious     = (Button)activity.findViewById(R.id.btnPrevious);
        btnShuffle      = (Button)activity.findViewById(R.id.btnShuffle);
        videoSeekBar    = (SeekBar)activity.findViewById(R.id.videoSeekBar);
        volumeSeekBar   = (SeekBar)activity.findViewById(R.id.volumeSeekBar);
        txtTitle        = (TextView)activity.findViewById(R.id.title);
        imgThumbnail    = (ImageView)activity.findViewById(R.id.thumbnail);

        btnPlayPause.setOnClickListener(PlaybackControls.this);
        btnStop.setOnClickListener(PlaybackControls.this);
        btnForward.setOnClickListener(PlaybackControls.this);
        btnRewind.setOnClickListener(PlaybackControls.this);
        btnMuteUnmute.setOnClickListener(PlaybackControls.this);
        btnRepeat.setOnClickListener(PlaybackControls.this);
        btnShuffle.setOnClickListener(PlaybackControls.this);
        btnNext.setOnClickListener(PlaybackControls.this);
        btnPrevious.setOnClickListener(PlaybackControls.this);
        videoSeekBar.setIndeterminate(false);
        videoSeekBar.setOnSeekBarChangeListener(PlaybackControls.this);
        volumeSeekBar.setIndeterminate(false);
        volumeSeekBar.setOnSeekBarChangeListener(PlaybackControls.this);
        videoSeekBar.getProgressDrawable().setColorFilter(getContext().getResources().getColor(R.color.T_GREY), PorterDuff.Mode.SRC_IN);
        videoSeekBar.getThumb().setColorFilter(getContext().getResources().getColor(R.color.SAFRON), PorterDuff.Mode.SRC_IN);
        volumeSeekBar.getProgressDrawable().setColorFilter(getContext().getResources().getColor(R.color.T_GREY), PorterDuff.Mode.SRC_IN);
        volumeSeekBar.getThumb().setColorFilter(getContext().getResources().getColor(R.color.SAFRON), PorterDuff.Mode.SRC_IN);

        isPlaying   = true;
        isMute      = false;
    }

    public static PlaybackControls getInstance(Context context){
        if(mInstance == null) {
            mInstance = new PlaybackControls(context);
        }
        return mInstance;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(CastStateMachineSingleton.getInstance().getCurrentCastState() != CastStates.CONNECTED) {
            Log.v(TAG, "onProgressChanged(): TV Disconnected!");
            return;
        }
        if(fromUser) {
            if(seekBar == videoSeekBar) {
                Log.v(TAG, "onProgressChanged(): progress: " + progress);
                videoProgress = progress;
            } else if(seekBar == volumeSeekBar) {
                Log.v(TAG, "onProgressChanged(): volume: " + progress);
                videoVolume = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(CastStateMachineSingleton.getInstance().getCurrentCastState() != CastStates.CONNECTED) {
            Log.v(TAG, "onStopTrackingTouch(): TV Disconnected");
            return;
        }
        if(seekBar == videoSeekBar) {
            videoSeekBar.setProgress(videoProgress);
            MediaLauncherSingleton.getInstance().seekTo(videoProgress);
        } else if(seekBar == volumeSeekBar) {
            volumeSeekBar.setProgress(videoVolume);
            MediaLauncherSingleton.getInstance().setVolume(videoVolume);
        }
    }

    @Override
    public void onClick(View view){
        Log.v(TAG, "onClick() called..");
        if(CastStateMachineSingleton.getInstance().getCurrentCastState() != CastStates.CONNECTED) {
            Log.v(TAG, "onClick(): TV Disconnected!");
            return;
        }
        if(!MediaLauncherSingleton.getInstance().isConnected()) {
            Log.v(TAG, "onClick(): isConnected: false");
            return;
        }
        if(view == view.findViewById(R.id.btnPlay)){
            if(isPlaying){
                Log.v(TAG, "onClick(): Pause");
                MediaLauncherSingleton.getInstance().pause();
            }
            else {
                Log.v(TAG, "onClick(): Play");
                MediaLauncherSingleton.getInstance().play();
            }
        }
        else if(view == view.findViewById(R.id.btnStop)){
            Log.v(TAG, "onClick(): Stop");
            MediaLauncherSingleton.getInstance().stop();
        }
        else if(view == view.findViewById(R.id.btnForward)){
            Log.v(TAG, "onClick(): Forward");
            MediaLauncherSingleton.getInstance().forward();
        }
        else if(view == view.findViewById(R.id.btnRewind)){
            Log.v(TAG, "onClick(): Rewind");
            MediaLauncherSingleton.getInstance().rewind();
        }
        else if(view == view.findViewById(R.id.btnMute)){
            if(isMute){
                Log.v(TAG, "onClick(): Unmute");
                MediaLauncherSingleton.getInstance().unmute();
            }
            else {
                Log.v(TAG, "onClick(): Mute");
                MediaLauncherSingleton.getInstance().mute();
            }
        }
        else if(view == view.findViewById(R.id.btnRepeat)) {
            Log.v(TAG, "onClick(): Repeat");
            MediaLauncherSingleton.getInstance().repeatQueue();
        }
        else if(view == view.findViewById(R.id.btnShuffle)) {
            Log.v(TAG, "onClick(): Shuffle");
            MediaLauncherSingleton.getInstance().shuffleQueue();
        }
        else if(view == view.findViewById(R.id.btnNext)) {
            Log.v(TAG, "onClick(): Next");
            MediaLauncherSingleton.getInstance().next();
        }
        else if(view == view.findViewById(R.id.btnPrevious)) {
            Log.v(TAG, "onClick(): Previous");
            MediaLauncherSingleton.getInstance().previous();
        }
    }

    //@Override
    public void onMediaBufferingStart() {
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                videoSeekBar.setSecondaryProgress(0);
            }
        });
    }

    //@Override
    public void onMediaBufferingComplete() {
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                videoSeekBar.setSecondaryProgress(videoSeekBar.getMax());
            }
        });
    }

    //@Override
    public void onMediaBufferingProgress(final int progress) {
        final int buffProgress = progress * (videoSeekBar.getMax() / 100);
                View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                videoSeekBar.setSecondaryProgress(buffProgress);
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    Log.w(TAG, "bufferProgress(): run(): Error!");
                }
            }
        });
    }

    //@Override
    public void onMediaCurrentPlayTime(final int progress) {
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                videoSeekBar.setProgress(progress);
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    Log.w(TAG, "videoProgress(): run(): Error!");
                }
            }
        });
    }

    //@Override
    public void onMediaVideoStreamStart(final int duration) {
        btnPlayPause.setBackground(getContext().getResources().getDrawable(R.drawable.pause));
        isPlaying = true;
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                videoSeekBar.setMax(duration);
                videoSeekBar.setProgress(0);
            }
        });
    }

    //@Override
    public void onMediaStreamCompleted() {
        btnPlayPause.setBackground(getContext().getResources().getDrawable(R.drawable.play));
        isPlaying = false;
    }

    //@Override
    public void onPlayerInitialized() {}

    //@Override
    public void onMediaPlay() {
        btnPlayPause.setBackground(getContext().getResources().getDrawable(R.drawable.pause));
        isPlaying = true;
    }

    //@Override
    public void onMediaPause() {
        btnPlayPause.setBackground(getContext().getResources().getDrawable(R.drawable.play));
        isPlaying = false;
    }

    //@Override
    public void onMediaStop() {
        btnPlayPause.setBackground(getContext().getResources().getDrawable(R.drawable.play));
        isPlaying = false;
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                videoSeekBar.setMax(0);
                videoSeekBar.setProgress(0);
                videoSeekBar.setSecondaryProgress(0);
            }
        });
    }

    //@Override
    public void onMediaForward() {}

    //@Override
    public void onMediaRewind() {}

    //@Override
    public void onMediaMute() {
        btnMuteUnmute.setBackground(getContext().getResources().getDrawable(R.drawable.unmute));
        isMute = true;
    }

    //@Override
    public void onMediaUnMute() {
        btnMuteUnmute.setBackground(getContext().getResources().getDrawable(R.drawable.mute));
        isMute = false;
    }

    //@Override
    public void onShuffle(boolean state) {
        if(state) {
            btnShuffle.setBackground(getContext().getResources().getDrawable(R.drawable.shuffle_on));
        } else {
            btnShuffle.setBackground(getContext().getResources().getDrawable(R.drawable.shuffle_off));
        }
    }

    //@Override
    public void onRepeat(VideoPlayer.RepeatMode mode) {
        if(mode.equals(VideoPlayer.RepeatMode.repeatOff)) {
            btnRepeat.setBackground(getContext().getResources().getDrawable(R.drawable.repeat_off));
        } else if(mode.equals(VideoPlayer.RepeatMode.repeatSingle)){
            btnRepeat.setBackground(getContext().getResources().getDrawable(R.drawable.repeat_single));
        } else if(mode.equals(VideoPlayer.RepeatMode.repeatAll)){
            btnRepeat.setBackground(getContext().getResources().getDrawable(R.drawable.repeat_all));
        }
    }

    //@Override
    public void onControlStatus(final int volLevel, final Boolean muteStatus, final Boolean shuffleStatus, final VideoPlayer.RepeatMode repeatStatus) {
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                volumeSeekBar.setProgress(volLevel);
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    Log.w(TAG, "onControlStatus(): run(): Error!");
                }
            }
        });

        if(muteStatus) { //true
            onMediaMute();
        } else {
            onMediaUnMute();
        }

        onShuffle(shuffleStatus);
        onRepeat(repeatStatus);
    }

    //@Override
    public void onVolumeChange(final int volLevel) {
        View view = mActivity.getCurrentFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                volumeSeekBar.setProgress(volLevel);
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    Log.w(TAG, "onVolumeChange(): run(): Error!");
                }
            }
        });
    }

    //@Override
    public void onCurrentPlaying(JSONObject data, String playerType) {
        try {
            if (playerType.equalsIgnoreCase(MediaLauncherSingleton.PlayerType.VIDEO.name())) {
                onCurrentPlaying(data.getString(MediaLauncherSingleton.TITLE),
                        Uri.parse(data.getString(MediaLauncherSingleton.VIDEO_THUMBNAIL_URL)),
                        MediaLauncherSingleton.PlayerType.VIDEO);
            } else if (playerType.equalsIgnoreCase(MediaLauncherSingleton.PlayerType.AUDIO.name())) {
                onCurrentPlaying(data.getString(MediaLauncherSingleton.TITLE),
                        Uri.parse(data.getString(MediaLauncherSingleton.AUDIO_ALBUM_ART)),
                        MediaLauncherSingleton.PlayerType.AUDIO);
            } else if (playerType.equalsIgnoreCase(MediaLauncherSingleton.PlayerType.PHOTO.name())) {
                onCurrentPlaying(data.getString(MediaLauncherSingleton.TITLE),
                        Uri.parse(data.getString(MediaLauncherSingleton.URL)),
                        MediaLauncherSingleton.PlayerType.PHOTO);
            } else {
                Picasso.with(mActivity.getApplicationContext())
                        .load(R.drawable.image_background)
                        .into(imgThumbnail);
            }
        } catch(Exception e) {
            Log.e(TAG, "Error while parsing jsonObject : " + e.getMessage()) ;
        }
    }

    public void onCurrentPlaying(String title, Uri thumbnailUrl, MediaLauncherSingleton.PlayerType playerType) {
        try {
            if (title != null) {
                txtTitle.setText(title);
            }

            //Fetch small thumbnail images for HD Images..
            String strThumbnailUrl = thumbnailUrl.toString();
            if(strThumbnailUrl.contains("samsungdforum.com/smartview/sample/image/photo_")) {
                strThumbnailUrl = strThumbnailUrl.replace(".jpg", "_small.jpg");
            }
            if(strThumbnailUrl.equals("")) {
                Picasso.with(mActivity.getApplicationContext())
                        .load(R.drawable.image_background)
                        .into(imgThumbnail);
            } else if (strThumbnailUrl != null) {
                Picasso.with(mActivity.getApplicationContext())
                        .load(strThumbnailUrl)
                        .error(R.drawable.image_background)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .into(imgThumbnail);
            } else {
                Picasso.with(mActivity.getApplicationContext())
                        .load(R.drawable.image_background)
                        .into(imgThumbnail);
            }

            //Disable controls w.r.t. playerType..
            if(playerType == MediaLauncherSingleton.PlayerType.VIDEO) {
                btnForward.setVisibility(View.VISIBLE);
                btnRewind.setVisibility(View.VISIBLE);
                btnRepeat.setVisibility(View.VISIBLE);
                btnShuffle.setVisibility(View.INVISIBLE);
                videoSeekBar.setVisibility(View.VISIBLE);
            } else if(playerType == MediaLauncherSingleton.PlayerType.AUDIO) {
                btnForward.setVisibility(View.INVISIBLE);
                btnRewind.setVisibility(View.INVISIBLE);
                btnRepeat.setVisibility(View.VISIBLE);
                btnShuffle.setVisibility(View.VISIBLE);
                videoSeekBar.setVisibility(View.VISIBLE);
            } else if(playerType == MediaLauncherSingleton.PlayerType.PHOTO) {
                btnForward.setVisibility(View.INVISIBLE);
                btnRewind.setVisibility(View.INVISIBLE);
                btnRepeat.setVisibility(View.INVISIBLE);
                btnShuffle.setVisibility(View.INVISIBLE);
                videoSeekBar.setVisibility(View.INVISIBLE);
            }
        } catch(Exception e) {
            Log.e(TAG, "Error while parsing jsonObject : " + e.getMessage()) ;
        }
    }

    public void resetPlaybackControls() {
        videoSeekBar.setProgress(0);
        videoSeekBar.setSecondaryProgress(0);
        volumeSeekBar.setProgress(0);
        txtTitle.setText("");
        Picasso.with(mActivity.getApplicationContext())
                .load(R.drawable.image_background)
                .into(imgThumbnail);
        btnPlayPause.setBackground(getContext().getResources().getDrawable(R.drawable.play));
        btnRepeat.setBackground(getContext().getResources().getDrawable(R.drawable.repeat_off));
        btnShuffle.setBackground(getContext().getResources().getDrawable(R.drawable.shuffle_off));
        btnMuteUnmute.setBackground(getContext().getResources().getDrawable(R.drawable.mute));
        btnForward.setVisibility(View.VISIBLE);
        btnRewind.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
        btnShuffle.setVisibility(View.VISIBLE);
        videoSeekBar.setVisibility(View.VISIBLE);
    }
}


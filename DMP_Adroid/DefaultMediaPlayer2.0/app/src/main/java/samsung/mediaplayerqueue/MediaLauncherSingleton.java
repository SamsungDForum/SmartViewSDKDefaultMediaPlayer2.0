package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Singleton class to store selected service & other related objects
 * It also handles all the callbacks from MediaPlayer and sets respective
 * callbacks for playbackControls to handle playback events.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.samsung.multiscreen.AudioPlayer;
import com.samsung.multiscreen.Channel;
import com.samsung.multiscreen.Client;
import com.samsung.multiscreen.Error;
import com.samsung.multiscreen.PhotoPlayer;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.VideoPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class MediaLauncherSingleton
    extends View {
    static final String TAG                      = "MediaLauncherSingleton";
    static final String URL                      = "uri";
    static final String TITLE                    = "title";
    static final String VIDEO_THUMBNAIL_URL      = "thumbnailUrl";
    static final String AUDIO_ALBUM_NAME         = "albumName";
    static final String AUDIO_ALBUM_ART          = "albumArt";

    private static MediaLauncherSingleton mInstance = null;
    private Service mService = null;
    private static VideoPlayer mVideoPlayer = null;
    private static AudioPlayer mAudioPlayer = null;
    private static PhotoPlayer mPhotoPlayer = null;
    private Settings mSettings = null;
    private Boolean mFirstPlayerLaunch = false;

    protected enum PlayerType {
        AUDIO,
        VIDEO,
        PHOTO,
        STANDBY
    }

    private PlayerType mPlayerType;

    private MediaLauncherSingleton(Context context){
        super(context);
    }

    private void initMediaPlayer() {
        final String playerName = getContext().getResources().getString(R.string.app_name);
        mVideoPlayer = this.mService.createVideoPlayer(playerName);
        mAudioPlayer = this.mService.createAudioPlayer(playerName);
        mPhotoPlayer = this.mService.createPhotoPlayer(playerName);

        //Set debug mode ON for library.
        mVideoPlayer.setDebug(true);
        mAudioPlayer.setDebug(true);
        mPhotoPlayer.setDebug(true);

        VideoPlayer.OnVideoPlayerListener videoPlayerListener = new VideoPlayer.OnVideoPlayerListener() {
            @Override
            public void onBufferingStart() {
                Log.v(TAG, "PlayerNotice: onBufferingStart V");
                PlaybackControls.getInstance(getContext()).onMediaBufferingStart();
            }

            @Override
            public void onBufferingComplete() {
                Log.v(TAG, "PlayerNotice: onBufferingComplete V");
                PlaybackControls.getInstance(getContext()).onMediaBufferingComplete();
            }

            @Override
            public void onBufferingProgress(int progress) {
                Log.v(TAG, "PlayerNotice: onBufferingProgress V: " + progress);
                PlaybackControls.getInstance(getContext()).onMediaBufferingProgress(progress);
            }

            @Override
            public void onCurrentPlayTime(int progress) {
                Log.v(TAG, "PlayerNotice: onCurrentPlayTime V: " + progress);
                PlaybackControls.getInstance(getContext()).onMediaCurrentPlayTime(progress);
            }

            @Override
            public void onStreamingStarted(int duration) {
                Log.v(TAG, "PlayerNotice: onStreamingStarted V: " + duration);
                PlaybackControls.getInstance(getContext()).onMediaVideoStreamStart(duration);
                getControlStatus();
            }

            @Override
            public void onStreamCompleted() {
                Log.v(TAG, "PlayerNotice: onStreamCompleted V");
                PlaybackControls.getInstance(getContext()).onMediaStreamCompleted();
            }

            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay V");
                PlaybackControls.getInstance(getContext()).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause V");
                PlaybackControls.getInstance(getContext()).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop V");
                PlaybackControls.getInstance(getContext()).onMediaStop();
            }

            @Override
            public void onForward() {
                Log.v(TAG, "PlayerNotice: onForward V");
                PlaybackControls.getInstance(getContext()).onMediaForward();
            }

            @Override
            public void onRewind() {
                Log.v(TAG, "PlayerNotice: onRewind V");
                PlaybackControls.getInstance(getContext()).onMediaRewind();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute V");
                PlaybackControls.getInstance(getContext()).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute V");
                PlaybackControls.getInstance(getContext()).onMediaUnMute();
            }

            @Override
            public void onNext() {
                Log.v(TAG, "PlayerNotice: onNext V");
            }

            @Override
            public void onPrevious() {
                Log.v(TAG, "PlayerNotice: onPrevious V");
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError V: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onAddToList V: " + enqueuedItem.toString());
                QueueSingleton.getInstance(getContext()).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onRemoveFromList V: " + dequeuedItem.toString());
                QueueSingleton.getInstance(getContext()).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onClearList V");
                QueueSingleton.getInstance(getContext()).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onGetList V: " + queueList.toString());
                QueueSingleton.getInstance(getContext()).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying V: " + currentItem.toString());
                PlaybackControls.getInstance(getContext()).onCurrentPlaying(currentItem, playerType);
                //Stop loader when 1st item starts playing.
                Loader.getInstance(getContext()).destroy();
                SwitchAppScreen.getInstance(getContext()).destroy();
            }

            @Override
            public void onRepeat(VideoPlayer.RepeatMode mode) {
                Log.v(TAG, "PlayerNotice: onRepeat V: " + mode.toString());
                PlaybackControls.getInstance(getContext()).onRepeat(mode);
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus, VideoPlayer.RepeatMode repeatStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus V: vol: " + volLevel + ", mute: " + muteStatus + ", repeat: " + repeatStatus.name());
                PlaybackControls.getInstance(getContext()).onControlStatus(volLevel, muteStatus, false, repeatStatus);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange V: " + level);
                PlaybackControls.getInstance(getContext()).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized V");
                //mAudioPlayer.removePlayerWatermark();
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange V");
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(getContext()).display();
                //reset all playback controls.
                PlaybackControls.getInstance(getContext()).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume V");
                SwitchAppScreen.getInstance(getContext()).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend V");
                SwitchAppScreen.getInstance(getContext()).display();
            }
        };
        mVideoPlayer.addOnMessageListener(videoPlayerListener);

        AudioPlayer.OnAudioPlayerListener audioPlayerListener = new AudioPlayer.OnAudioPlayerListener() {
            @Override
            public void onBufferingStart() {
                Log.v(TAG, "PlayerNotice: onBufferingStart A");
                PlaybackControls.getInstance(getContext()).onMediaBufferingStart();
            }

            @Override
            public void onBufferingComplete() {
                Log.v(TAG, "PlayerNotice: onBufferingComplete A");
                PlaybackControls.getInstance(getContext()).onMediaBufferingComplete();
            }

            @Override
            public void onBufferingProgress(int progress) {
                Log.v(TAG, "PlayerNotice: onBufferingProgress A: " + progress);
                PlaybackControls.getInstance(getContext()).onMediaBufferingProgress(progress);
            }

            @Override
            public void onCurrentPlayTime(int progress) {
                Log.v(TAG, "PlayerNotice: onCurrentPlayTime A: " + progress);
                PlaybackControls.getInstance(getContext()).onMediaCurrentPlayTime(progress);
            }

            @Override
            public void onStreamingStarted(int duration) {
                Log.v(TAG, "PlayerNotice: onStreamingStarted A: " + duration);
                PlaybackControls.getInstance(getContext()).onMediaVideoStreamStart(duration);
                getControlStatus();
            }

            @Override
            public void onStreamCompleted() {
                Log.v(TAG, "PlayerNotice: onStreamCompleted A");
                PlaybackControls.getInstance(getContext()).onMediaStreamCompleted();
            }

            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay A");
                PlaybackControls.getInstance(getContext()).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause A");
                PlaybackControls.getInstance(getContext()).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop A");
                PlaybackControls.getInstance(getContext()).onMediaStop();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute A");
                PlaybackControls.getInstance(getContext()).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute A");
                PlaybackControls.getInstance(getContext()).onMediaUnMute();
            }

            @Override
            public void onNext() {
                Log.v(TAG, "PlayerNotice: onNext A");
            }

            @Override
            public void onPrevious() {
                Log.v(TAG, "PlayerNotice: onPrevious A");
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError A: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onEnqueue A: " + enqueuedItem.toString());
                QueueSingleton.getInstance(getContext()).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onDequeue A: " + dequeuedItem.toString());
                QueueSingleton.getInstance(getContext()).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onQueueClear A");
                QueueSingleton.getInstance(getContext()).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onQueueFetch A: " + queueList.toString());
                QueueSingleton.getInstance(getContext()).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying A: " + currentItem.toString());
                PlaybackControls.getInstance(getContext()).onCurrentPlaying(currentItem, playerType);
                //Stop loader when 1st item starts playing.
                Loader.getInstance(getContext()).destroy();
                SwitchAppScreen.getInstance(getContext()).destroy();
            }

            @Override
            public void onShuffle(Boolean state) {
                Log.v(TAG, "PlayerNotice: onShuffle A: " + state.toString());
                PlaybackControls.getInstance(getContext()).onShuffle(state);
            }

            @Override
            public void onRepeat(VideoPlayer.RepeatMode mode) {
                Log.v(TAG, "PlayerNotice: onRepeat A : " + mode.toString());
                PlaybackControls.getInstance(getContext()).onRepeat(mode);
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus, Boolean shuffleStatus, VideoPlayer.RepeatMode repeatStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus A: vol: " + volLevel + ", mute: " + muteStatus + ", shuffle: " + shuffleStatus + ", repeat: " + repeatStatus.name());
                PlaybackControls.getInstance(getContext()).onControlStatus(volLevel, muteStatus, shuffleStatus, repeatStatus);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange A: " + level);
                PlaybackControls.getInstance(getContext()).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized A");
                /*
                 * This is a special case for Player Initialization; Since we are using audio player's
                 * object to launch standby screen - we use audioPlayer's object to set watermark.
                 */
                try {
                    String watermarkUrl = mSettings.getString(getContext().getResources().getString(R.string.watermarkUrl));
                    if (!mFirstPlayerLaunch && watermarkUrl != null && watermarkUrl != "") {
                        mAudioPlayer.setPlayerWatermark(Uri.parse(watermarkUrl));
                        mFirstPlayerLaunch = true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onPlayerInitialized() : Exception : " + e.getMessage());
                }
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange A");
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(getContext()).display();
                //reset all playback controls.
                PlaybackControls.getInstance(getContext()).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume A");
                SwitchAppScreen.getInstance(getContext()).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend A");
                SwitchAppScreen.getInstance(getContext()).display();
            }
        };
        mAudioPlayer.addOnMessageListener(audioPlayerListener);

        PhotoPlayer.OnPhotoPlayerListener photoPlayerListener = new PhotoPlayer.OnPhotoPlayerListener() {
            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay P");
                PlaybackControls.getInstance(getContext()).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause P");
                PlaybackControls.getInstance(getContext()).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop P");
                PlaybackControls.getInstance(getContext()).onMediaStop();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute P");
                PlaybackControls.getInstance(getContext()).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute P");
                PlaybackControls.getInstance(getContext()).onMediaUnMute();
            }

            @Override
            public void onNext() {
                Log.v(TAG, "PlayerNotice: onNext P");
            }

            @Override
            public void onPrevious() {
                Log.v(TAG, "PlayerNotice: onPrevious P");
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError P: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onEnqueue P: " + enqueuedItem.toString());
                QueueSingleton.getInstance(getContext()).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onDequeue P: " + dequeuedItem.toString());
                QueueSingleton.getInstance(getContext()).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onQueueClear P");
                QueueSingleton.getInstance(getContext()).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onQueueFetch P: " + queueList.toString());
                QueueSingleton.getInstance(getContext()).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying P: " + currentItem.toString());
                PlaybackControls.getInstance(getContext()).onCurrentPlaying(currentItem, playerType);
                //Since, we do not get any event to mark the trigger of photo display,
                //we have to toggle play button to pause here..
                PlaybackControls.getInstance(getContext()).onMediaPlay();
                //Stop loader when 1st item starts playing.
                Loader.getInstance(getContext()).destroy();
                SwitchAppScreen.getInstance(getContext()).destroy();
                getControlStatus();
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus P: vol: " + volLevel + ", mute: " + muteStatus);
                PlaybackControls.getInstance(getContext()).onControlStatus(volLevel, muteStatus, false, PhotoPlayer.RepeatMode.repeatOff);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange P: " + level);
                PlaybackControls.getInstance(getContext()).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized P");
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange P");
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(getContext()).display();
                //reset all playback controls.
                PlaybackControls.getInstance(getContext()).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume P");
                SwitchAppScreen.getInstance(getContext()).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend P");
                SwitchAppScreen.getInstance(getContext()).display();
            }
        };
        mPhotoPlayer.addOnMessageListener(photoPlayerListener);

        /*
         * Launch standbyScreen ~ as per settings in the settings menu..
         */
        mSettings = Settings.getInstance(getContext());

        if(mSettings.getBool(getContext().getString(R.string.showStandbyScreen))) {
            String bgImage1 = mSettings.getString(getContext().getString(R.string.bgImageUrl1));
            String bgImage2 = mSettings.getString(getContext().getString(R.string.bgImageUrl2));
            String bgImage3 = mSettings.getString(getContext().getString(R.string.bgImageUrl3));

            try {
                Uri bgImageUri1;
                if(bgImage1 != null && bgImage1.length() > 0) {
                    bgImageUri1 = Uri.parse(bgImage1);
                } else {
                    bgImageUri1 = null;
                }
                Uri bgImageUri2;
                if(bgImage2 != null && bgImage2.length() > 0) {
                    bgImageUri2 = Uri.parse(bgImage2);
                } else {
                    bgImageUri2 = null;
                }
                Uri bgImageUri3;
                if(bgImage3 != null && bgImage3.length() > 0) {
                    bgImageUri3 = Uri.parse(bgImage3);
                } else {
                    bgImageUri3 = null;
                }

                mAudioPlayer.standbyConnect(bgImageUri1, bgImageUri2, bgImageUri3, new Result<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Log.d(TAG, "standbyConnect(): success : ");
                        mPlayerType = PlayerType.STANDBY;
                    }

                    @Override
                    public void onError(com.samsung.multiscreen.Error error) {
                        Log.e(TAG, "standbyConnect(): error : " + error.getMessage());
                    }
                });
            } catch(Exception e) {
                Log.e(TAG, "initMediaPlayer() : exception : " + e.getMessage());
            }
        }
    }

    static MediaLauncherSingleton getInstance(Context context) {
        if(null == mInstance){
            mInstance = new MediaLauncherSingleton(context);
        }
        return mInstance;
    }

    void setService(final Service service){
        service.isDMPSupported(new Result<Boolean>() {
            @Override
            public void onSuccess(Boolean isSupported) {
                if (isSupported) {
                    mService = service;
                    initMediaPlayer();
                    CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.CONNECTED);
                } else {
                    Toast.makeText(getContext(), "DMP NOT supported by TV!", Toast.LENGTH_SHORT).show();
                    CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                }
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
            }
        });
    }

    private void resetService(){
        this.mService = null;
        CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
    }

    Boolean isConnected() {
        if(mPlayerType == PlayerType.VIDEO) {
            return (mVideoPlayer.isConnected());
        } else if(mPlayerType == PlayerType.AUDIO) {
            return (mAudioPlayer.isConnected());
        } else if(mPlayerType == PlayerType.PHOTO) {
            return (mPhotoPlayer.isConnected());
        } else if(mPlayerType == PlayerType.STANDBY) {
            return (mAudioPlayer.isConnected());
        } else {
            return false;
        }
    }

    void disconnect() {
        mFirstPlayerLaunch = false;
        if(getContext() == null) {
            CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
            return;
        }
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.disconnect(mSettings.getBool(getContext().getResources().getString(R.string.closeOnDisconnect)), new Result<Client>() {
                @Override
                public void onError(com.samsung.multiscreen.Error error) {
                    Log.v(TAG, "disconnect(): Error: " + error);
                }

                @Override
                public void onSuccess(Client client) {
                    Log.v(TAG, "disconnect(): Success: " + client);
                    CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                }
            });
        } else if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.disconnect(mSettings.getBool(getContext().getResources().getString(R.string.closeOnDisconnect)), new Result<Client>() {
                @Override
                public void onError(com.samsung.multiscreen.Error error) {
                    Log.v(TAG, "disconnect(): Error: " + error);
                }

                @Override
                public void onSuccess(Client client) {
                    Log.v(TAG, "disconnect(): Success: " + client);
                    CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                }
            });
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.disconnect(mSettings.getBool(getContext().getResources().getString(R.string.closeOnDisconnect)), new Result<Client>() {
                @Override
                public void onError(com.samsung.multiscreen.Error error) {
                    Log.v(TAG, "disconnect(): Error: " + error);
                }

                @Override
                public void onSuccess(Client client) {
                    Log.v(TAG, "disconnect(): Success: " + client);
                    CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                }
            });
        } else if(mPlayerType == PlayerType.STANDBY) {
            if(mSettings.getBool(getContext().getString(R.string.showStandbyScreen))) {
                mAudioPlayer.removePlayerWatermark();
            }
            mAudioPlayer.disconnect(mSettings.getBool(getContext().getResources().getString(R.string.closeOnDisconnect)), new Result<Client>() {
                @Override
                public void onError(com.samsung.multiscreen.Error error) {
                    Log.v(TAG, "disconnect(): Error: " + error);
                }

                @Override
                public void onSuccess(Client client) {
                    Log.v(TAG, "disconnect(): Success: " + client);
                    CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                }
            });
        }
    }

    /*public Service getService(){
        return this.mService;
    }*/

    /**
     * Method to play content on T.V.
     * @param uri : Url of content which has to be launched on TV.
     * @param title : title of the audio.
     * @param albumName : album name of the song/audio.
     * @param albumArt : album art url.
     */
    void playContent(final String uri,
                            final String title,
                            final String albumName,
                            final String albumArt) {
        if (null != mAudioPlayer && null != mService) {
            Log.v(TAG, "Playing Content: " + uri);
            mAudioPlayer.playContent(Uri.parse(uri),
                title,
                albumName,           /*albumName*/
                Uri.parse(albumArt), /*albumArtUrl*/
                new Result<Boolean>() {
                    @Override
                    public void onSuccess(Boolean r) {
                        Log.v(TAG, "playContent(): onSuccess.");
                        if(mPlayerType != PlayerType.AUDIO) {
                            /*Since, is case of standby connect, we can not show queue(list) to the user, we wait for 1st song to be played before showing the list items.*/
                            if(mPlayerType == PlayerType.STANDBY) {
                                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
                            }
                            mPlayerType = PlayerType.AUDIO;
                        }
                    }

                    @Override
                    public void onError(com.samsung.multiscreen.Error error) {
                        Log.v(TAG, "playContent(): onError: " + error.getMessage());
                        Toast.makeText(getContext(), "Error in Launching Content!", Toast.LENGTH_SHORT).show();
                    }
                });
        } else {
            Log.v(TAG, "playContent(): un-initialized mAudioPlayer.");
        }

        mAudioPlayer.setOnConnectListener(new Channel.OnConnectListener() {
            @Override
            public void onConnect(Client client) {
                Log.v(TAG, "setOnConnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
            }
        });

        mAudioPlayer.setOnDisconnectListener(new Channel.OnDisconnectListener() {
            @Override
            public void onDisconnect(Client client) {
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(getContext()).destroy();
                resetService();
            }
        });

        mAudioPlayer.setOnErrorListener(new Channel.OnErrorListener() {
            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "setOnErrorListener() called: Error: " + error.getMessage());
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
            }
        });
    }

    /**
     * Method to play content on T.V.
     * @param uri : Url of content which has to be launched on TV.
     * @param title : title of the photo.
     */
    void playContent(final String uri,
                            final String title) {
        if (null != mPhotoPlayer && null != mService) {
            mPhotoPlayer.playContent(Uri.parse(uri),
                    title,
                    new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean r) {
                            Log.v(TAG, "playContent(): onSuccess.");
                            if(mPlayerType != PlayerType.PHOTO) { //To force setting these at only 1st photo launch..
                                /*Since, is case of standby connect, we can not show queue(list) to the user, we wait for 1st photo to be displayed before showing the list items.*/
                                if(mPlayerType == PlayerType.STANDBY) {
                                    ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
                                }
                                mPlayerType = PlayerType.PHOTO;
                                String music = mSettings.getString(getContext().getResources().getString(R.string.bgAudioUrl));
                                mPhotoPlayer.setBackgroundMusic(Uri.parse(music));
                            }
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            Log.v(TAG, "playContent(): onError: " + error.getMessage());
                            Toast.makeText(getContext(), "Error in Launching Content!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.v(TAG, "playContent(): un-initialized mPhotoPlayer.");
        }

        mPhotoPlayer.setOnConnectListener(new Channel.OnConnectListener() {
            @Override
            public void onConnect(Client client) {
                Log.v(TAG, "setOnConnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
            }
        });

        mPhotoPlayer.setOnDisconnectListener(new Channel.OnDisconnectListener() {
            @Override
            public void onDisconnect(Client client) {
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(getContext()).destroy();
                resetService();
            }
        });

        mPhotoPlayer.setOnErrorListener(new Channel.OnErrorListener() {
            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "setOnErrorListener() called: Error: " + error.getMessage());
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
            }
        });
    }

    /**
     * Method to play content on T.V.
     * @param uri : Url of content which has to be launched on TV.
     * @param title : title of the video.
     * @param thumbnail : Thumbnail url.
     */
    void playContent(final String uri,
                            final String title,
                            final String thumbnail) {
        if (null != mVideoPlayer && null != mService) {
            Log.v(TAG, "Playing Content: " + uri);
            mVideoPlayer.playContent(Uri.parse(uri),
                    title,
                    Uri.parse(thumbnail),
                    new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean r) {
                            Log.v(TAG, "playContent(): onSuccess.");
                            if(mPlayerType != PlayerType.VIDEO) {
                                /*Since, is case of standby connect, we can not show queue(list) to the user, we wait for 1st video to be played before showing the list items.*/
                                if(mPlayerType == PlayerType.STANDBY) {
                                    ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
                                }
                                mPlayerType = PlayerType.VIDEO;
                            }
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            Log.v(TAG, "playContent(): onError: " + error.getMessage());
                            Toast.makeText(getContext(), "Error in Launching Content!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.v(TAG, "playContent(): un-initialized mVideoPlayer.");
        }

        mVideoPlayer.setOnConnectListener(new Channel.OnConnectListener() {
            @Override
            public void onConnect(Client client) {
                Log.v(TAG, "setOnConnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
            }
        });

        mVideoPlayer.setOnDisconnectListener(new Channel.OnDisconnectListener() {
            @Override
            public void onDisconnect(Client client) {
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(getContext()).destroy();
                resetService();
            }
        });

        mVideoPlayer.setOnErrorListener(new Channel.OnErrorListener() {
            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "setOnErrorListener() called: Error: " + error.getMessage());
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
            }
        });
    }

    /*playback controls*/
    void play(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.play();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.play();
        } else {
            mAudioPlayer.play();
        }
    }

    void pause(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.pause();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.pause();
        } else {
            mAudioPlayer.pause();
        }
    }

    void stop(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.stop();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.stop();
        } else {
            mAudioPlayer.stop();
        }
    }

    void forward(){
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.forward();
        }
    }

    void rewind(){
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.rewind();
        }
    }

    void mute(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.mute();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.mute();
        } else {
            mAudioPlayer.mute();
        }
    }

    void unmute(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.unMute();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.unMute();
        } else {
            mAudioPlayer.unMute();
        }
    }

    void enqueue(final Uri uri,
                        final String title,
                        final Uri thumbnailUrl) {
        mVideoPlayer.addToList(uri, title, thumbnailUrl);
    }

    void enqueue(final Uri uri,
                        final String title,
                        final String albumName,
                        final Uri albumArt) {
            mAudioPlayer.addToList(uri, title, albumName, albumArt);
    }

    void enqueue(final Uri uri,
                        final String title) {
        mPhotoPlayer.addToList(uri, title);
    }

    void enqueue(final List<Map<String, String>> list, PlayerType playerType) {
        if(playerType == PlayerType.VIDEO) {
            mVideoPlayer.addToList(list);
        } else if(playerType == PlayerType.AUDIO) {
            mAudioPlayer.addToList(list);
        } else if(playerType == PlayerType.PHOTO) {
            mPhotoPlayer.addToList(list);
        }
    }

    void dequeue(final Uri uri) {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.removeFromList(uri);
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.removeFromList(uri);
        } else {
            mAudioPlayer.removeFromList(uri);
        }
    }

    void fetchQueue() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.getList();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.getList();
        } else {
            mAudioPlayer.getList();
        }
    }

    void clearQueue() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.clearList();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.clearList();
        } else {
            mAudioPlayer.clearList();
        }
    }

    void repeatQueue() {
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.repeat();
        } else {
            mAudioPlayer.repeat();
        }
    }

    void shuffleQueue() {
        if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.shuffle();
        }
    }

    void seekTo(int progress) {
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.seekTo(progress, TimeUnit.MILLISECONDS);
        } else if(mPlayerType == PlayerType.AUDIO){
            mAudioPlayer.seekTo(progress, TimeUnit.MILLISECONDS);
        }
    }

    void getControlStatus() {
        if(mPlayerType == PlayerType.PHOTO) {
            Log.d(TAG, "getControlStatus called for photoPlayer");
            mPhotoPlayer.getControlStatus();
        } else if(mPlayerType == PlayerType.VIDEO) {
            Log.d(TAG, "getControlStatus called for videoPlayer");
            mVideoPlayer.getControlStatus();
        } else if(mPlayerType == PlayerType.AUDIO) {
            Log.d(TAG, "getControlStatus called for audioPlayer");
            mAudioPlayer.getControlStatus();
        }
    }

    void setVolume(int level) {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.setVolume(level);
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.setVolume(level);
        } else {
            mAudioPlayer.setVolume(level);
        }
    }

    void volumeUp() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.volumeUp();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.volumeUp();
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.volumeUp();
        }
    }

    void volumeDown() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.volumeDown();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.volumeDown();
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.volumeDown();
        }
    }

    void next() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.next();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.next();
        } else {
            mAudioPlayer.next();
        }
    }

    void setRepeat(AudioPlayer.RepeatMode mode) {
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.setRepeat(mode);
        } else if(mPlayerType == PlayerType.AUDIO){
            mAudioPlayer.setRepeat(mode);
        }
    }

    void setShuffle(Boolean mode) {
        if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.setShuffle(mode);
        }
    }

    void previous() {
        if (mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.previous();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.previous();
        } else {
            mAudioPlayer.previous();
        }
    }

    void resumeApplicationInForeground() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.resumeApplicationInForeground();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.resumeApplicationInForeground();
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.resumeApplicationInForeground();
        }
    }
}
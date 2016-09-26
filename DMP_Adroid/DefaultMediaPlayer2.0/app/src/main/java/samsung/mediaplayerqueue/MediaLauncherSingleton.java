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
import android.widget.Toast;

import com.samsung.multiscreen.AudioPlayer;
import com.samsung.multiscreen.Channel;
import com.samsung.multiscreen.Client;
import com.samsung.multiscreen.Device;
import com.samsung.multiscreen.PhotoPlayer;
import com.samsung.multiscreen.Player;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.VideoPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MediaLauncherSingleton
        //implements PlayerNoticeHandler
{
    public static final String TAG                      = "MediaLauncherSingleton";
    public static final String URL                      = "uri";
    public static final String TITLE                    = "title";
    public static final String VIDEO_THUMBNAIL_URL      = "thumbnailUrl";
    public static final String AUDIO_ALBUM_NAME         = "albumName";
    public static final String AUDIO_ALBUM_ART          = "albumArt";

    private static MediaLauncherSingleton mInstance = null;
    private Service mService = null;
    private static VideoPlayer mVideoPlayer = null;
    private static AudioPlayer mAudioPlayer = null;
    private static PhotoPlayer mPhotoPlayer = null;
    private Context mContext = null;

    protected enum PlayerType {
        AUDIO,
        VIDEO,
        PHOTO
    };
    protected PlayerType mPlayerType;

    private MediaLauncherSingleton(){}

    private void initMediaPlayer()
    {
        final String playerName = mContext.getResources().getString(R.string.app_name);
        mVideoPlayer = this.mService.createVideoPlayer(playerName);
        mAudioPlayer = this.mService.createAudioPlayer(playerName);
        mPhotoPlayer = this.mService.createPhotoPlayer(playerName);

        //Set debug mode ON for library.
        mVideoPlayer.setDebug(true);
        mAudioPlayer.setDebug(true);
        mPhotoPlayer.setDebug(true);

        mService.getDeviceInfo(new Result<Device>() {
            @Override
            public void onSuccess(Device device) {
                String deviceModel = device.getModel();
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.e(TAG, "getDeviceInfo()");
            }
        });

        VideoPlayer.OnVideoPlayerListener videoPlayerListener = new VideoPlayer.OnVideoPlayerListener() {
            @Override
            public void onBufferingStart() {
                Log.v(TAG, "PlayerNotice: onBufferingStart V");
                PlaybackControls.getInstance(mContext).onMediaBufferingStart();
            }

            @Override
            public void onBufferingComplete() {
                Log.v(TAG, "PlayerNotice: onBufferingComplete V");
                PlaybackControls.getInstance(mContext).onMediaBufferingComplete();
            }

            @Override
            public void onBufferingProgress(int progress) {
                Log.v(TAG, "PlayerNotice: onBufferingProgress V: " + progress);
                PlaybackControls.getInstance(mContext).onMediaBufferingProgress(progress);
            }

            @Override
            public void onCurrentPlayTime(int progress) {
                Log.v(TAG, "PlayerNotice: onCurrentPlayTime V: " + progress);
                PlaybackControls.getInstance(mContext).onMediaCurrentPlayTime(progress);
            }

            @Override
            public void onStreamingStarted(int duration) {
                Log.v(TAG, "PlayerNotice: onStreamingStarted V: " + duration);
                PlaybackControls.getInstance(mContext).onMediaVideoStreamStart(duration);
                getControlStatus();
            }

            @Override
            public void onStreamCompleted() {
                Log.v(TAG, "PlayerNotice: onStreamCompleted V");
                PlaybackControls.getInstance(mContext).onMediaStreamCompleted();
            }

            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay V");
                PlaybackControls.getInstance(mContext).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause V");
                PlaybackControls.getInstance(mContext).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop V");
                PlaybackControls.getInstance(mContext).onMediaStop();
            }

            @Override
            public void onForward() {
                Log.v(TAG, "PlayerNotice: onForward V");
                PlaybackControls.getInstance(mContext).onMediaForward();
            }

            @Override
            public void onRewind() {
                Log.v(TAG, "PlayerNotice: onRewind V");
                PlaybackControls.getInstance(mContext).onMediaRewind();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute V");
                PlaybackControls.getInstance(mContext).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute V");
                PlaybackControls.getInstance(mContext).onMediaUnMute();
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError V: " + error.getMessage());
                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onAddToList V: " + enqueuedItem.toString());
                QueueSingleton.getInstance(mContext).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onRemoveFromList V: " + dequeuedItem.toString());
                QueueSingleton.getInstance(mContext).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onClearList V");
                QueueSingleton.getInstance(mContext).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onGetList V: " + queueList.toString());
                QueueSingleton.getInstance(mContext).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying V: " + currentItem.toString());
                PlaybackControls.getInstance(mContext).onCurrentPlaying(currentItem, playerType);
                //Stop loader when 1st item starts playing.
                Loader.getInstance(mContext).destroy();
                SwitchAppScreen.getInstance(mContext).destroy();
            }

            /*@Override
            public void onShuffle(Boolean state) {
                Log.v(TAG, "PlayerNotice: onShuffle V: " + state.toString());
                PlaybackControls.getInstance(mContext).onShuffle(state);
            }*/

            @Override
            public void onRepeat(VideoPlayer.RepeatMode mode) {
                Log.v(TAG, "PlayerNotice: onRepeat V: " + mode.toString());
                PlaybackControls.getInstance(mContext).onRepeat(mode);
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus, VideoPlayer.RepeatMode repeatStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus V: vol: " + volLevel + ", mute: " + muteStatus + ", repeat: " + repeatStatus.name());
                PlaybackControls.getInstance(mContext).onControlStatus(volLevel, muteStatus, false, repeatStatus);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange V: " + level);
                PlaybackControls.getInstance(mContext).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized V");
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange V");
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(mContext).display();
                //reset all playback controls.
                PlaybackControls.getInstance(mContext).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume V");
                SwitchAppScreen.getInstance(mContext).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend V");
                SwitchAppScreen.getInstance(mContext).display();
            }
        };
        mVideoPlayer.addOnMessageListener(videoPlayerListener);

        AudioPlayer.OnAudioPlayerListener audioPlayerListener = new AudioPlayer.OnAudioPlayerListener() {
            @Override
            public void onBufferingStart() {
                Log.v(TAG, "PlayerNotice: onBufferingStart A");
                PlaybackControls.getInstance(mContext).onMediaBufferingStart();
            }

            @Override
            public void onBufferingComplete() {
                Log.v(TAG, "PlayerNotice: onBufferingComplete A");
                PlaybackControls.getInstance(mContext).onMediaBufferingComplete();
            }

            @Override
            public void onBufferingProgress(int progress) {
                Log.v(TAG, "PlayerNotice: onBufferingProgress A: " + progress);
                PlaybackControls.getInstance(mContext).onMediaBufferingProgress(progress);
            }

            @Override
            public void onCurrentPlayTime(int progress) {
                Log.v(TAG, "PlayerNotice: onCurrentPlayTime A: " + progress);
                PlaybackControls.getInstance(mContext).onMediaCurrentPlayTime(progress);
            }

            @Override
            public void onStreamingStarted(int duration) {
                Log.v(TAG, "PlayerNotice: onStreamingStarted A: " + duration);
                PlaybackControls.getInstance(mContext).onMediaVideoStreamStart(duration);
                getControlStatus();
            }

            @Override
            public void onStreamCompleted() {
                Log.v(TAG, "PlayerNotice: onStreamCompleted A");
                PlaybackControls.getInstance(mContext).onMediaStreamCompleted();
            }

            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay A");
                PlaybackControls.getInstance(mContext).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause A");
                PlaybackControls.getInstance(mContext).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop A");
                PlaybackControls.getInstance(mContext).onMediaStop();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute A");
                PlaybackControls.getInstance(mContext).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute A");
                PlaybackControls.getInstance(mContext).onMediaUnMute();
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError A: " + error.getMessage());
                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onEnqueue A: " + enqueuedItem.toString());
                QueueSingleton.getInstance(mContext).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onDequeue A: " + dequeuedItem.toString());
                QueueSingleton.getInstance(mContext).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onQueueClear A");
                QueueSingleton.getInstance(mContext).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onQueueFetch A: " + queueList.toString());
                QueueSingleton.getInstance(mContext).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying A: " + currentItem.toString());
                PlaybackControls.getInstance(mContext).onCurrentPlaying(currentItem, playerType);
                //Stop loader when 1st item starts playing.
                Loader.getInstance(mContext).destroy();
                SwitchAppScreen.getInstance(mContext).destroy();
            }

            @Override
            public void onShuffle(Boolean state) {
                Log.v(TAG, "PlayerNotice: onShuffle A: " + state.toString());
                PlaybackControls.getInstance(mContext).onShuffle(state);
            }

            @Override
            public void onRepeat(VideoPlayer.RepeatMode mode) {
                Log.v(TAG, "PlayerNotice: onRepeat A : " + mode.toString());
                PlaybackControls.getInstance(mContext).onRepeat(mode);
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus, Boolean shuffleStatus, VideoPlayer.RepeatMode repeatStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus A: vol: " + volLevel + ", mute: " + muteStatus + ", shuffle: " + shuffleStatus + ", repeat: " + repeatStatus.name());
                PlaybackControls.getInstance(mContext).onControlStatus(volLevel, muteStatus, shuffleStatus, repeatStatus);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange A: " + level);
                PlaybackControls.getInstance(mContext).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized A");
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange A");
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(mContext).display();
                //reset all playback controls.
                PlaybackControls.getInstance(mContext).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume A");
                SwitchAppScreen.getInstance(mContext).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend A");
                SwitchAppScreen.getInstance(mContext).display();
            }
        };
        mAudioPlayer.addOnMessageListener(audioPlayerListener);

        PhotoPlayer.OnPhotoPlayerListener photoPlayerListener = new PhotoPlayer.OnPhotoPlayerListener() {
            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay P");
                PlaybackControls.getInstance(mContext).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause P");
                PlaybackControls.getInstance(mContext).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop P");
                PlaybackControls.getInstance(mContext).onMediaStop();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute P");
                PlaybackControls.getInstance(mContext).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute P");
                PlaybackControls.getInstance(mContext).onMediaUnMute();
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError P: " + error.getMessage());
                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onEnqueue P: " + enqueuedItem.toString());
                QueueSingleton.getInstance(mContext).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onDequeue P: " + dequeuedItem.toString());
                QueueSingleton.getInstance(mContext).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onQueueClear P");
                QueueSingleton.getInstance(mContext).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onQueueFetch P: " + queueList.toString());
                QueueSingleton.getInstance(mContext).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying P: " + currentItem.toString());
                PlaybackControls.getInstance(mContext).onCurrentPlaying(currentItem, playerType);
                //Since, we do not get any event to mark the trigger of photo display,
                //we have to toggle play button to pause here..
                PlaybackControls.getInstance(mContext).onMediaPlay();
                //Stop loader when 1st item starts playing.
                Loader.getInstance(mContext).destroy();
                SwitchAppScreen.getInstance(mContext).destroy();
                getControlStatus();
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus P: vol: " + volLevel + ", mute: " + muteStatus);
                PlaybackControls.getInstance(mContext).onControlStatus(volLevel, muteStatus, false, Player.RepeatMode.repeatOff);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange P: " + level);
                PlaybackControls.getInstance(mContext).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized P");
                mPhotoPlayer.setSlideTimeout(10000); //in milliseconds.
                mPhotoPlayer.setBackgroundMusic(Uri.parse("https://www.samsungdforum.com/smartview/sample/audio/Beverly_-_01_-_You_Said_It.mp3"));
                getControlStatus();
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange P");
                mPhotoPlayer.setSlideTimeout(10000); //in milliseconds.
                mPhotoPlayer.setBackgroundMusic(Uri.parse("https://www.samsungdforum.com/smartview/sample/audio/Beverly_-_01_-_You_Said_It.mp3"));
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(mContext).display();
                //reset all playback controls.
                PlaybackControls.getInstance(mContext).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume P");
                SwitchAppScreen.getInstance(mContext).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend P");
                SwitchAppScreen.getInstance(mContext).display();
            }
        };
        mPhotoPlayer.addOnMessageListener(photoPlayerListener);
    }

    public static MediaLauncherSingleton getInstance() {
        if(null == mInstance){
            mInstance = new MediaLauncherSingleton();
        }
        return mInstance;
    }

    public void setService(final Context context, final Service service){
        this.mService = service;
        this.mContext = context;
        initMediaPlayer();
        CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.CONNECTED);
    }

    private void resetService(){
        this.mService = null;
        this.mContext = null;
        CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
    }

    public Boolean isConnected() {
        if(mPlayerType == PlayerType.VIDEO) {
            return (mVideoPlayer.isConnected());
        } else if(mPlayerType == PlayerType.AUDIO) {
            return (mAudioPlayer.isConnected());
        } else if(mPlayerType == PlayerType.PHOTO) {
            return (mPhotoPlayer.isConnected());
        } else {
            return false;
        }
    }

    public void disconnect() {
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.disconnect(false, new Result<Client>() {
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
            mAudioPlayer.disconnect(false, new Result<Client>() {
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
            mPhotoPlayer.disconnect(false, new Result<Client>() {
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
    public void playContent(final String uri,
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
                            mPlayerType = PlayerType.AUDIO;
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            Log.v(TAG, "playContent(): onError: " + error.getMessage());
                            Toast.makeText(mContext, "Error in Launching Content!", Toast.LENGTH_SHORT).show();
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
                resetService();
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(mContext).destroy();
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
    public void playContent(final String uri,
                            final String title) {
        if (null != mPhotoPlayer && null != mService) {
            mPhotoPlayer.playContent(Uri.parse(uri),
                    title,
                    new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean r) {
                            Log.v(TAG, "playContent(): onSuccess.");
                            mPlayerType = PlayerType.PHOTO;
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            Log.v(TAG, "playContent(): onError: " + error.getMessage());
                            Toast.makeText(mContext, "Error in Launching Content!", Toast.LENGTH_SHORT).show();
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
                resetService();
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(mContext).destroy();
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
    public void playContent(final String uri,
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
                            mPlayerType = PlayerType.VIDEO;
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            Log.v(TAG, "playContent(): onError: " + error.getMessage());
                            Toast.makeText(mContext, "Error in Launching Content!", Toast.LENGTH_SHORT).show();
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
                resetService();
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(mContext).destroy();
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
    public void play(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.play();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.play();
        } else {
            mAudioPlayer.play();
        }
    }

    public void pause(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.pause();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.pause();
        } else {
            mAudioPlayer.pause();
        }
    }

    public void stop(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.stop();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.stop();
        } else {
            mAudioPlayer.stop();
        }
    }

    public void forward(){
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.forward();
        }
    }

    public void rewind(){
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.rewind();
        }
    }

    public void mute(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.mute();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.mute();
        } else {
            mAudioPlayer.mute();
        }
    }

    public void unmute(){
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.unMute();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.unMute();
        } else {
            mAudioPlayer.unMute();
        }
    }

    public void enqueue(final Uri uri,
                        final String title,
                        final Uri thumbnailUrl) {
        mVideoPlayer.addToList(uri, title, thumbnailUrl);
    }

    public void enqueue(final Uri uri,
                        final String title,
                        final String albumName,
                        final Uri albumArt) {
        mAudioPlayer.addToList(uri, title, albumName, albumArt);
    }

    public void enqueue(final Uri uri,
                        final String title) {
        mPhotoPlayer.addToList(uri, title);
    }

    public void enqueue(final List<Map<String, String>> list, PlayerType playerType) {
        if(playerType == PlayerType.VIDEO) {
            mVideoPlayer.addToList(list);
        } else if(playerType == PlayerType.AUDIO) {
            mAudioPlayer.addToList(list);
        } else if(playerType == PlayerType.PHOTO) {
            mPhotoPlayer.addToList(list);
        }
    }

    public void dequeue(final Uri uri) {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.removeFromList(uri);
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.removeFromList(uri);
        } else {
            mAudioPlayer.removeFromList(uri);
        }
    }

    public void fetchQueue() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.getList();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.getList();
        } else {
            mAudioPlayer.getList();
        }
    }

    public void clearQueue() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.clearList();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.clearList();
        } else {
            mAudioPlayer.clearList();
        }
    }

    public void repeatQueue() {
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.repeat();
        } else {
            mAudioPlayer.repeat();
        }
    }

    public void shuffleQueue() {
        if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.shuffle();
        }
    }

    public void seekTo(int progress) {
        if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.seekTo(progress, TimeUnit.MILLISECONDS);
        } else if(mPlayerType == PlayerType.AUDIO){
            mAudioPlayer.seekTo(progress, TimeUnit.MILLISECONDS);
        }
    }

    public void getControlStatus() {
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

    public void setVolume(int level) {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.setVolume(level);
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.setVolume(level);
        } else {
            mAudioPlayer.setVolume(level);
        }
    }

    public void volumeUp() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.volumeUp();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.volumeUp();
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.volumeUp();
        }
    }

    public void volumeDown() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.volumeDown();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.volumeDown();
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.volumeDown();
        }
    }

    public void next() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.next();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.next();
        } else {
            mAudioPlayer.next();
        }
    }

    public void previous() {
        if (mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.previous();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.previous();
        } else {
            mAudioPlayer.previous();
        }
    }

    public void resumeApplicationInForeground() {
        if(mPlayerType == PlayerType.PHOTO) {
            mPhotoPlayer.resumeApplicationInForeground();
        } else if(mPlayerType == PlayerType.VIDEO) {
            mVideoPlayer.resumeApplicationInForeground();
        } else if(mPlayerType == PlayerType.AUDIO) {
            mAudioPlayer.resumeApplicationInForeground();
        }
    }
}
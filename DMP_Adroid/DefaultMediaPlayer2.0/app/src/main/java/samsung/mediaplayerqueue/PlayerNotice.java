package samsung.mediaplayerqueue;

import com.samsung.multiscreen.VideoPlayer;

import org.json.JSONObject;

/**
 * @author Ankit Saini
 * Interface class for PLayerNotice notification handler.
 */
interface PlayerNotice {
    void onMediaPlay();
    void onMediaPause();
    void onMediaStop();
    void onMediaForward();
    void onMediaRewind();
    void onMediaMute();
    void onMediaUnMute();
    void onShuffle(boolean state);
    void onRepeat(VideoPlayer.RepeatMode mode);
    void onGetVolume(int level);

    void onMediaBufferingStart();
    void onMediaBufferingComplete();
    void onMediaBufferingProgress(int progress);
    void onMediaCurrentPlayTime(int progress);
    void onMediaVideoStreamStart(int duration);
    void onMediaStreamCompleted();
    void onCurrentPlaying(JSONObject data, String playerType);
    void onPlayerInitialized();
}

package samsung.mediaplayerqueue;

import com.samsung.multiscreen.VideoPlayer;

import org.json.JSONObject;

/**
 * @author Ankit Saini
 * Interface class for PLayerNotice notification handler.
 */
public interface PlayerNotice {
    public void onMediaPlay();
    public void onMediaPause();
    public void onMediaStop();
    public void onMediaForward();
    public void onMediaRewind();
    public void onMediaMute();
    public void onMediaUnMute();
    public void onShuffle(boolean state);
    public void onRepeat(VideoPlayer.RepeatMode mode);
    public void onGetVolume(int level);

    public void onMediaBufferingStart();
    public void onMediaBufferingComplete();
    public void onMediaBufferingProgress(int progress);
    public void onMediaCurrentPlayTime(int progress);
    public void onMediaVideoStreamStart(int duration);
    public void onMediaStreamCompleted();
    public void onCurrentPlaying(JSONObject data, String playerType);
    public void onPlayerInitialized();
}

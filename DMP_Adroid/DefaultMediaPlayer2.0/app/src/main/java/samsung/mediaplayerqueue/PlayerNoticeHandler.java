package samsung.mediaplayerqueue;

import com.samsung.multiscreen.VideoPlayer;

/**
 * @author Ankit Saini
 * Interface class for registering & deregistering to PlayerNotice notifications.
 */
public interface PlayerNoticeHandler {
    public void registerPlayerNoticeEvent(PlayerNotice observer);
    public void removePlayerNoticeEvent(PlayerNotice observer);

    public void playerNoticePlayObserver();
    public void playerNoticePauseObserver();
    public void playerNoticeStopObserver();
    public void playerNoticeForwardObserver();
    public void playerNoticeRewindObserver();
    public void playerNoticeMuteObserver();
    public void playerNoticeUnMuteObserver();
    public void playerNoticeRepeatObserver(VideoPlayer.RepeatMode mode);
    public void playerNoticeShuffleObserver(Boolean state);
    public void playerNoticeControlStatusObserver(int volLevel, Boolean muteStatus, Boolean shuffleStatus, VideoPlayer.RepeatMode repeatStatus);

    public void playerNoticeBufferingStartObserver();
    public void playerNoticeBufferingCompleteObserver();
    public void playerNoticeBufferingProgressObserver(int progress);
    public void playerNoticeCurrentPlayTimeObserver(int progress);
    public void playerNoticeVideoStreamStartObserver(int progress);
    public void playerNoticeStreamCompletedObserver();
    public void playerVoticePlayerInitializedObserver();
}

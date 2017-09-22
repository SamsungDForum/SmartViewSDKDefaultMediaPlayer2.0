package samsung.mediaplayerqueue;

import com.samsung.multiscreen.VideoPlayer;

/**
 * @author Ankit Saini
 * Interface class for registering & deregistering to PlayerNotice notifications.
 */
interface PlayerNoticeHandler {
    void registerPlayerNoticeEvent(PlayerNotice observer);
    void removePlayerNoticeEvent(PlayerNotice observer);

    void playerNoticePlayObserver();
    void playerNoticePauseObserver();
    void playerNoticeStopObserver();
    void playerNoticeForwardObserver();
    void playerNoticeRewindObserver();
    void playerNoticeMuteObserver();
    void playerNoticeUnMuteObserver();
    void playerNoticeRepeatObserver(VideoPlayer.RepeatMode mode);
    void playerNoticeShuffleObserver(Boolean state);
    void playerNoticeControlStatusObserver(int volLevel, Boolean muteStatus, Boolean shuffleStatus, VideoPlayer.RepeatMode repeatStatus);

    void playerNoticeBufferingStartObserver();
    void playerNoticeBufferingCompleteObserver();
    void playerNoticeBufferingProgressObserver(int progress);
    void playerNoticeCurrentPlayTimeObserver(int progress);
    void playerNoticeVideoStreamStartObserver(int progress);
    void playerNoticeStreamCompletedObserver();
    void playerVoticePlayerInitializedObserver();
}

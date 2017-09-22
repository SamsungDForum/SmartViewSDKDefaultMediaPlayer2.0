package samsung.mediaplayerqueue;

import android.util.Log;
import android.widget.Toast;

import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.VideoPlayer;

import org.json.JSONArray;
import org.json.JSONObject;


class Video {

    private VideoPlayer mVideoPlayer;
    private static final String TAG                      = "Video";

    Video(Service service, String playerName) {
        mVideoPlayer = service.createVideoPlayer(playerName);
    }

    mVideoPlayer.setDebug(true);

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
}
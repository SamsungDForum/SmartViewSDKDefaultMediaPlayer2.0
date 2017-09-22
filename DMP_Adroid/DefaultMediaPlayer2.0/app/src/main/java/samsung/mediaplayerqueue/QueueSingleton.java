package samsung.mediaplayerqueue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Ankit Saini
 * Manages - THE Q.
 */
class QueueSingleton {
    private static final String TAG = "QueueSingleton";

    private static QueueSingleton mInstance = null;
    private QueueAdapter mQueueAdapter = null;

    private QueueSingleton(Context context) {
        mQueueAdapter = new QueueAdapter(context, R.layout.layout_queue);
    }

    void onEnqueue(JSONObject data, MediaLauncherSingleton.PlayerType playerType) {
        if(data == null) {
            return;
        }
        QueueItem item = null;

        try {
            if(playerType == MediaLauncherSingleton.PlayerType.VIDEO) {
                item = new QueueItem(
                        data.getString(MediaLauncherSingleton.URL),
                        data.getString(MediaLauncherSingleton.TITLE),
                        data.getString(MediaLauncherSingleton.VIDEO_THUMBNAIL_URL));
            } else if(playerType == MediaLauncherSingleton.PlayerType.AUDIO) {
                item = new QueueItem(
                        data.getString(MediaLauncherSingleton.URL),
                        data.getString(MediaLauncherSingleton.TITLE),
                        data.getString(MediaLauncherSingleton.AUDIO_ALBUM_ART));
            } else if(playerType == MediaLauncherSingleton.PlayerType.PHOTO) {
                item = new QueueItem(
                        data.getString(MediaLauncherSingleton.URL),
                        data.getString(MediaLauncherSingleton.TITLE),
                        data.getString(MediaLauncherSingleton.URL));
            }
        } catch (Exception e) {
            Log.e(TAG, "Enqueue: Error in parsing video data: " + e);
        }
        if (null == item) {
            return;
        }

        /*
        * Though, we are not supposed to get duplicate enqueue items,
        * but in a special scenerio, we get duplicate items from TV. Thus, to keep
        * this in check, we have to check - whether this item already exists in the
        * list. If it does, we replace with new item info.
        */
        int tempItemIndex = mQueueAdapter.getItemPosition(item.contentUrl);
        if(tempItemIndex >= 0) {
            QueueItem tempItem = mQueueAdapter.getItem(tempItemIndex);
            mQueueAdapter.remove(tempItem);
        }
        mQueueAdapter.add(item);
        notifyDataChange();
    }

    void onDequeue(JSONObject data) {
        if(data == null) {
            return;
        }

        if(mQueueAdapter.isEmpty()) {
            return;
        }

        String videoUrl = null;
        try {
            videoUrl = data.getString(MediaLauncherSingleton.URL);
        } catch (Exception e) {
            Log.e(TAG, "Dequeue: Error in parsing data: " + e);
        }

        if(null == videoUrl) {
            return;
        }

        int index = mQueueAdapter.getItemPosition(videoUrl);
        QueueItem item = mQueueAdapter.getItem(index);
        mQueueAdapter.remove(item);
        notifyDataChange();
    }

    void onClearQueue() {
        if(mQueueAdapter.isEmpty()) {
            return;
        }

        mQueueAdapter.clear();
        notifyDataChange();
    }

    void onFetchQueue(JSONArray data, MediaLauncherSingleton.PlayerType playerType) {
        if(data == null) {
            return;
        }

        mQueueAdapter.clear();
        QueueItem item = null;

        for(int i = 0; i < data.length(); ++i) {
            try {
                if(playerType == MediaLauncherSingleton.PlayerType.VIDEO) {
                    item = new QueueItem(data.getJSONObject(i).getString(MediaLauncherSingleton.URL),
                            data.getJSONObject(i).getString(MediaLauncherSingleton.TITLE),
                            data.getJSONObject(i).getString(MediaLauncherSingleton.VIDEO_THUMBNAIL_URL));
                } else if(playerType == MediaLauncherSingleton.PlayerType.AUDIO) {
                    item = new QueueItem(data.getJSONObject(i).getString(MediaLauncherSingleton.URL),
                            data.getJSONObject(i).getString(MediaLauncherSingleton.TITLE),
                            data.getJSONObject(i).getString(MediaLauncherSingleton.AUDIO_ALBUM_ART));
                } else if(playerType == MediaLauncherSingleton.PlayerType.PHOTO) {
                    //Fetch small thumbnail images for HD Images..
                    String strThumbnailUrl = data.getJSONObject(i).getString(MediaLauncherSingleton.URL);
                    if(strThumbnailUrl.contains("developer.samsung.com")) {
                        strThumbnailUrl = strThumbnailUrl.replace(".jpg", "_small.jpg");
                    }
                    item = new QueueItem(data.getJSONObject(i).getString(MediaLauncherSingleton.URL),
                            data.getJSONObject(i).getString(MediaLauncherSingleton.TITLE),
                            strThumbnailUrl);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in parsing json data : " + e.getMessage());
            }
            if(item != null) {
                mQueueAdapter.add(item);
            }
        }
    }

    private void notifyDataChange() {
        Handler mQueueHandler = new Handler();
        mQueueHandler.post(new Runnable() {
            @Override
            public void run() {
                mQueueAdapter.notifyDataSetChanged();
            }
        });
    }

    QueueAdapter getQueueAdapter() {
        return mQueueAdapter;
    }

    static QueueSingleton getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new QueueSingleton(context);
        }
        return mInstance;
    }
}

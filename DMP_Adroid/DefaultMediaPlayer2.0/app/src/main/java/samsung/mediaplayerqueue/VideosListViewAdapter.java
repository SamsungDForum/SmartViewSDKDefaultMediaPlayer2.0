package samsung.mediaplayerqueue;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Ankit Saini
 * Adapter class to populate video Items in Videos tab (Listview).
 */
public class VideosListViewAdapter extends ArrayAdapter<VideoItem> {
    private final static String TAG = "videosListViewAdapter";
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<VideoItem> mData = new ArrayList<VideoItem>();

    public VideosListViewAdapter(Context context, int layoutResourceId, ArrayList<VideoItem> data)
    {
        super(context, layoutResourceId, data);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
    }

    static class ViewHolder {
        ImageView thumbNailImage;
        TextView videoTitle;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        ViewHolder holder = null;

        if(null == rowView)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            rowView = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.thumbNailImage = (ImageView)rowView.findViewById(R.id.video_thumbnail);
            holder.videoTitle = (TextView)rowView.findViewById(R.id.video_label);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }

        final VideoItem item = mData.get(position);
        /*Download the image from url and display on image view..*/
        Log.v(TAG, "Video Thumbnail URL : " + item.thumbnailUrl);
        if(item.thumbnailUrl.isEmpty()) {
            //holder.thumbNailImage.setImageDrawable(null);
            Picasso.with(mContext)
                    .load(R.drawable.thumbnail)
                    .into(holder.thumbNailImage);
        } else {
            Picasso.with(mContext)
                    .load(item.thumbnailUrl)
                    .error(R.drawable.thumbnail)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.thumbNailImage);
        }
        holder.videoTitle.setText(item.videoTitle);

        //Set Enqueue onClick button event.
        Button btnEnqueue = null;
        btnEnqueue = (Button)rowView.findViewById(R.id.btnEnqueue);

        if(btnEnqueue != null) {
            btnEnqueue.setVisibility(View.VISIBLE);
            btnEnqueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri videoUrl = null;
                    Uri thumbnailUrl = null;
                    Uri subtitleUrl = null;

                    if (item.videoUrl != null) {
                        videoUrl = Uri.parse(item.videoUrl);
                    } else {
                        videoUrl = Uri.parse("");
                    }
                    if (item.thumbnailUrl != null) {
                        thumbnailUrl = Uri.parse(item.thumbnailUrl);
                    } else {
                        thumbnailUrl = Uri.parse("");
                    }
                    if (CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {
                        MediaLauncherSingleton.getInstance(). enqueue(videoUrl,
                                item.videoTitle,
                                thumbnailUrl);
                    } else {
                        Toast.makeText(mContext, "Please connect to a TV.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return rowView;
    }

    /**
     * Updates grid data and refresh grid items..
     * @param data
     */
    public void setData(ArrayList<VideoItem> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
}

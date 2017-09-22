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
 * Adapter class to populate audio Items in Audios tab (Listview).
 */
class AudiosListViewAdapter extends ArrayAdapter<AudioItem> {
    private final static String TAG = "AudiosListViewAdapter";
    private int mLayoutResourceId;
    private ArrayList<AudioItem> mData = new ArrayList<AudioItem>();

    AudiosListViewAdapter(Context context, int layoutResourceId, ArrayList<AudioItem> data)
    {
        super(context, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
    }

    private static class ViewHolder {
        TextView audioTitle;
        TextView albumName;
        ImageView albumArt;
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
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            rowView = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.audioTitle = (TextView)rowView.findViewById(R.id.audio_title);
            holder.albumName = (TextView)rowView.findViewById(R.id.audio_albumName);
            holder.albumArt = (ImageView)rowView.findViewById(R.id.audio_albumArt);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }

        final AudioItem item = mData.get(position);
        /*Download the image from url and display on image view..*/
        Log.v(TAG, "Album Art URL : " + item.albumArt);
        if(item.albumArt.isEmpty()) {
            Picasso.with(getContext())
                    .load(R.drawable.thumbnail)
                    .into(holder.albumArt);
        } else {
            Picasso.with(getContext())
                    .load(item.albumArt)
                    .error(R.drawable.thumbnail)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.albumArt);
        }
        holder.audioTitle.setText(item.audioTitle);
        holder.albumName.setText(item.albumName);

        //Set Enqueue onClick button event.
        Button btnEnqueue = null;
        btnEnqueue = (Button)rowView.findViewById(R.id.btnEnqueue);

        if(btnEnqueue != null) {
            btnEnqueue.setVisibility(View.VISIBLE);
            btnEnqueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri audioUrl = null;
                    Uri albumArt = null;

                    if (item.audioUrl != null) {
                        audioUrl = Uri.parse(item.audioUrl);
                    } else {
                        audioUrl = Uri.parse("");
                    }
                    if (item.albumArt != null) {
                        albumArt = Uri.parse(item.albumArt);
                    } else {
                        albumArt = Uri.parse("");
                    }

                    //send enqueue request..
                    if (CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {
                        MediaLauncherSingleton.getInstance(getContext()).enqueue(audioUrl,
                                item.audioTitle,
                                item.albumName,
                                albumArt);
                    } else {
                        Toast.makeText(getContext(), "Please connect to a TV.", Toast.LENGTH_SHORT).show();
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
    void setData(ArrayList<AudioItem> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
}

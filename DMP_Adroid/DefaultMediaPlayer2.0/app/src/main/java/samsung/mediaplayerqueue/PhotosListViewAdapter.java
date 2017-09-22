package samsung.mediaplayerqueue;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
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
 * Adapter class to populate photo Items in Photos tab (Listview).
 */
class PhotosListViewAdapter extends ArrayAdapter<PhotoItem> {
    private final static String TAG = "PhotosListViewAdapter";
    private int mLayoutResourceId;
    private ArrayList<PhotoItem> mData = new ArrayList<PhotoItem>();

    PhotosListViewAdapter(Context context, int layoutResourceId, ArrayList<PhotoItem> data)
    {
        super(context, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
    }

    private static class ViewHolder {
        ImageView thumbNailImage;
        TextView photoTitle;
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
            holder.thumbNailImage = (ImageView)rowView.findViewById(R.id.photo_thumbnail);
            holder.photoTitle = (TextView)rowView.findViewById(R.id.photo_title);
            rowView.setTag(holder);

            // Set photo thumbnail height & width dynamically..
            Activity activity = (Activity)getContext();
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            int columnLength = screenWidth / 3;

            holder.thumbNailImage.getLayoutParams().height = columnLength;
            holder.thumbNailImage.getLayoutParams().width = columnLength;
            holder.photoTitle.getLayoutParams().width = columnLength;
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }

        final PhotoItem item = mData.get(position);
        /*Download the image from url and display on image view..*/
        //Fetch small thumbnail images for HD Images..
        String thumbnailUrl = item.photoUrl;
        if(thumbnailUrl.contains("developer.samsung.com")) {
            thumbnailUrl = thumbnailUrl.replace(".jpg", "_small.jpg");
        }
        Log.v(TAG, "Photo Thumbnail URL : " + thumbnailUrl);
        if(thumbnailUrl.isEmpty()) {
            //holder.thumbNailImage.setImageDrawable(null);
            Picasso.with(getContext())
                    .load(R.drawable.thumbnail)
                    .into(holder.thumbNailImage);
        } else {
            Picasso.with(getContext())
                    .load(thumbnailUrl)
                    .error(R.drawable.thumbnail)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.thumbNailImage);
        }
        holder.photoTitle.setText(item.photoTitle);

        //Set Enqueue onClick button event.
        Button btnEnqueue = null;
        btnEnqueue = (Button)rowView.findViewById(R.id.btnEnqueue);

        if(btnEnqueue != null) {
            btnEnqueue.setVisibility(View.VISIBLE);
            btnEnqueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri photoUrl = null;
                    if (item.photoUrl != null) {
                        photoUrl = Uri.parse(item.photoUrl);
                    } else {
                        photoUrl = Uri.parse("");
                    }
                    if (CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {
                        MediaLauncherSingleton.getInstance(getContext()). enqueue(photoUrl,
                                item.photoTitle);
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
    void setData(ArrayList<PhotoItem> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
}

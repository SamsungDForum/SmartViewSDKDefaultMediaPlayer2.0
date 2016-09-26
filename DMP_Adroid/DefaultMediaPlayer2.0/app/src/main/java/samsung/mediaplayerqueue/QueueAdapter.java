package samsung.mediaplayerqueue;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

/**
 * @author Ankit Saini
 * THE Q Adapter.
 */
public class QueueAdapter extends ArrayAdapter<QueueItem> {

    private Context mContext;
    private int mLayoutResourceId;
    private LayoutInflater mInflater;

    public QueueAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.mContext = context;
        this.mLayoutResourceId = resourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean contains(QueueItem item) {
        return (getPosition(item) >= 0);
    }

    public void replace(QueueItem item) {
        int position = getPosition(item);
        if (position >= 0)
        {
            remove(item);
            insert(item, position);
        }
    }

    class ViewHolder {
        ImageView   thumbnail;
        TextView    title;
    }

    public int getItemPosition(String url) {
        int position = -1;
        for(int i = 0; i < getCount(); ++i) {
            QueueItem temp = getItem(i);

            //String(Url) compare..
            boolean isEqual = true;
            char[] s1 = temp.contentUrl.toCharArray();
            char[] s2 = url.toCharArray();
            int l1 = s1.length;
            int l2 = s2.length;
            if(l1 == l2) {
                for(int j = 0; j < l1; ++j) {
                    if(s1[j] != s2[j]) {
                        isEqual = false;
                        break;
                    }
                }
            } else {
                isEqual = false;
            }

            if(isEqual) {
                position = i;
            }
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(mLayoutResourceId, parent, false);
            holder.thumbnail    = (ImageView)convertView.findViewById(R.id.q_item_thumbnail);
            holder.title        = (TextView)convertView.findViewById(R.id.q_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final QueueItem qItem = getItem(position);
        if(qItem.contentthumbUrl.isEmpty()) {
            Picasso.with(mContext)
                    .load(R.drawable.thumbnail)
                    .into(holder.thumbnail);
        } else {
            Picasso.with(mContext)
                    .load(qItem.contentthumbUrl)
                    .error(R.drawable.thumbnail)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.thumbnail);
        }
        holder.title.setText(qItem.contentTitle);

        //Set Dequeue onClick button event.
        Button btnDequeue = null;
        btnDequeue = (Button)convertView.findViewById(R.id.btnDequeue);

        if(btnDequeue != null) {
            btnDequeue.setVisibility(View.VISIBLE);
            btnDequeue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri contentUrl = null;

                    if (qItem.contentUrl != null) {
                        contentUrl = Uri.parse(qItem.contentUrl);
                    } else {
                        contentUrl = Uri.parse("");
                    }

                    MediaLauncherSingleton.getInstance().dequeue(contentUrl);
                }
            });
        }

        return convertView;
    }
}

package samsung.mediaplayerqueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samsung.multiscreen.Device;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Service;

/**
 * @author Ankit Saini
 * Adapter class to populate tv list (services).
 */
public class TVListAdapter extends ArrayAdapter<Service>
{
    private Context mContext;
    private int mLayoutResourceId;
    private LayoutInflater mInflater;

    public TVListAdapter(Context context, int resourceId)
    {
        super(context, resourceId);
        this.mContext = context;
        this.mLayoutResourceId = resourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean contains(Service service)
    {
        return (getPosition(service) >= 0);
    }

    public void replace(Service service)
    {
        int position = getPosition(service);
        if (position >= 0)
        {
            remove(service);
            insert(service, position);
        }
    }

    class ViewHolder
    {
        TextView name;
        TextView details;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;

        if (convertView == null)
        {
            holder = new ViewHolder();

            convertView = mInflater.inflate(mLayoutResourceId, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.tvName);
            holder.details = (TextView) convertView.findViewById(R.id.tvDetals);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Service service = getItem(position);
        holder.name.setText(service.getName());
        service.getDeviceInfo(new Result<Device>() {
            @Override
            public void onSuccess(Device device) {
                String details = "[" + device.getIp() + "] [" + device.getModel() + "] [" + device.getNetworkType() + "]";
                holder.details.setText(details);
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
            }
        });


        return convertView;
    }
}

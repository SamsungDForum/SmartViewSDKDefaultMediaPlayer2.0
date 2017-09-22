package samsung.mediaplayerqueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samsung.multiscreen.Service;

/**
 * @author Ankit Saini
 * Adapter class to populate tv list (services).
 */
class TVListAdapter extends ArrayAdapter<Service>
{
    private int mLayoutResourceId;
    private LayoutInflater mInflater;

    TVListAdapter(Context context, int resourceId)
    {
        super(context, resourceId);
        this.mLayoutResourceId = resourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    boolean contains(Service service)
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

    private class ViewHolder
    {
        TextView name;
        TextView details;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;

        if (convertView == null)
        {
            holder = new ViewHolder();

            convertView = mInflater.inflate(mLayoutResourceId, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.tvName);
            //holder.details = (TextView) convertView.findViewById(R.id.tvDetals);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final Service service = getItem(position);
        if(service == null) return convertView;
        holder.name.setText(service.getName());

        /*service.getDeviceInfo(new Result<Device>() {
            @Override
            public void onSuccess(Device device) {
                String details = "[" + device.getIp() + "] [" + device.getModel() + "] [" + device.getNetworkType() + "]";
                holder.details.setText(details);
                Log.d("Ankit", "TV [" + position + "] : " + holder.name.getText() + ": " + holder.details.getText());
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                String details = "Standby TV";
                holder.details.setText(details);
                Log.d("Ankit", "TV [" + position + "] : " + holder.name.getText() + ": " + holder.details.getText());
            }
        });*/
        return convertView;
    }
}

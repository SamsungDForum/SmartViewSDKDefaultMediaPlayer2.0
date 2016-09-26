package samsung.mediaplayerqueue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;

/**
 * @author Ankit Saini
 * Instantiates tv discovery and populate TVListAdapter with available services (TVs).
 */
public class TVSearch {
    private static String TAG = "TVSearch";
    private Search mTVSearch = null;
    private Context mContext = null;
    private TVListAdapter mTVListAdapter = null;
    private Handler mTVLsitHandler = new Handler();

    private void init(Context context) {
            mContext = context;
            mTVListAdapter = new TVListAdapter(mContext, R.layout.layout_tvlist_item);
    }

    /*
     * Method to notify TV List data change.
     */
    private void notifyDataChange() {
        mTVLsitHandler.post(new Runnable() {
            @Override
            public void run() {
                mTVListAdapter.notifyDataSetChanged();
            }
        });
    }

    /*
     * Method to update (add) new service (tv) to ListView adapter.
     */
    private void updateTVList(Service service) {
        if(null == service)
        {
            Log.w(TAG, "updateTVList(): NULL service!!!");
            return;
        }

        /*If service already doesn't exist in TVListAdapter, add it*/
        if(!mTVListAdapter.contains(service))
        {
            mTVListAdapter.add(service);
            Log.v(TAG, "TVListAdapter.add(service): " + service);
            notifyDataChange();
        }
    }

    /*Start TV Discovery*/
    public void startDiscovery() {
        if(mContext == null || mTVListAdapter == null) {
            Log.w(TAG, "Can't start Discovery.");
            return;
        }

        if(null == mTVSearch)
        {
            mTVSearch = Service.search(mContext);
            Log.v(TAG, "Device (" + mTVSearch + ") Search instantiated..");
            mTVSearch.setOnServiceFoundListener(new Search.OnServiceFoundListener() {
                @Override
                public void onFound(Service service) {
                    Log.v(TAG, "setOnServiceFoundListener(): onFound(): Service Added: " + service);
                    updateTVList(service);
                }
            });

            mTVSearch.setOnStartListener(new Search.OnStartListener() {
                @Override
                public void onStart() {
                    Log.v(TAG, "Starting Discovery.");
                }
            });

            mTVSearch.setOnStopListener(new Search.OnStopListener() {
                @Override
                public void onStop() {
                    Log.v(TAG, "Discovery Stopped.");
                }
            });

            mTVSearch.setOnServiceLostListener(new Search.OnServiceLostListener() {
                @Override
                public void onLost(Service service) {
                    Log.v(TAG, "Discovery: Service Lost!!!");
                    /*remove TV*/
                    if (null == service) {
                        return;
                    }
                    mTVListAdapter.remove(service);
                    notifyDataChange();
                }
            });
        }

        boolean bStartDiscovery = mTVSearch.start();
        if(bStartDiscovery)
        {
            Log.v(TAG, "Discovery Already Started..");
        }
        else
        {
            Log.v(TAG, "New Discovery Started..");
        }
    }

    /* Stop TV Discovery*/
    public void stopDiscovery() {
        if (null != mTVSearch)
        {
            mTVSearch.stop();
            mTVSearch = null;
            Log.v(TAG, "Stopping Discovery.");
        }
        if(CastStateMachineSingleton.getInstance().getCurrentCastState() != CastStates.CONNECTED) {
            CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
        }
    }

    TVSearch(Context context){
        init(context);
    }

    public TVListAdapter getTVListAdapter() {
        return mTVListAdapter;
    }

    public boolean isSearching() {
        return mTVSearch.isSearching();
    }

}

package samsung.mediaplayerqueue;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;

/**
 * @author Ankit Saini
 * Instantiates device discovery and populate DeviceListAdapter with available services.
 */
class TVSearch extends View {
    private static String TAG = "TVSearch";
    private Search mSearch = null;
    private TVListAdapter mDeviceListAdapter = null;
    private Handler mDeviceListHandler = new Handler();
    private static TVSearch mInstance = null;

    private TVSearch(Context context) {
        super(context);
        mDeviceListAdapter = new TVListAdapter(context, R.layout.layout_tvlist_item);
    }

    /*
     * Method to notify TV List data change.
     */
    private void notifyDataChange() {
        mDeviceListHandler.post(new Runnable() {
            @Override
            public void run() {
                mDeviceListAdapter.notifyDataSetChanged();
            }
        });
    }

    /*
     * Method to update (add) new service (tv) to ListView adapter.
     */
    private void updateTVList(final Service service) {
        if(null == service)
        {
            Log.w(TAG, "updateTVList(): NULL service!!!");
            return;
        }

        /*If service already doesn't exist in TVListAdapter, add it*/
        if(!mDeviceListAdapter.contains(service))
        {
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDeviceListAdapter.add(service);
                    Log.v(TAG, "TVListAdapter.add(service): " + service);
                    notifyDataChange();
                }
            });
        }
    }

    static TVSearch getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new TVSearch(context);
        }
        return mInstance;
    }

    /*Start TV Discovery*/
    void startDiscovery(Boolean showStandbyDevices) {
        if(getContext() == null || mDeviceListAdapter == null) {
            Log.w(TAG, "Can't start Discovery.");
            return;
        }

        if(mSearch == null)
        {
            mSearch = Service.search(getContext());

            Log.v(TAG, "Device (" + mSearch + ") Search instantiated..");
            mSearch.setOnServiceFoundListener(new Search.OnServiceFoundListener() {
                @Override
                public void onFound(Service service) {
                    Log.v(TAG, "setOnServiceFoundListener(): onFound(): Service Added: " + service);
                    updateTVList(service);
                }
            });

            mSearch.setOnStartListener(new Search.OnStartListener() {
                @Override
                public void onStart() {
                    Log.v(TAG, "Starting Discovery.");
                }
            });

            mSearch.setOnStopListener(new Search.OnStopListener() {
                @Override
                public void onStop() {
                    Log.v(TAG, "Discovery Stopped.");
                }
            });

            mSearch.setOnServiceLostListener(new Search.OnServiceLostListener() {
                @Override
                public void onLost(Service service) {
                    Log.v(TAG, "Discovery: Service Lost!!!");
                    /*remove TV*/
                    if (null == service) {
                        return;
                    }
                    mDeviceListAdapter.remove(service);
                    notifyDataChange();
                }
            });
        }

        boolean bStartDiscovery = mSearch.start(showStandbyDevices);
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
    void stopDiscovery() {
        if (null != mSearch)
        {
            mSearch.stop();
            //mSearch = null;
            mDeviceListAdapter.clear();
            Log.v(TAG, "Stopping Discovery.");
        }
        if(CastStateMachineSingleton.getInstance().getCurrentCastState() != CastStates.CONNECTED) {
            CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
        }
    }

    TVListAdapter getTVListAdapter() {
        return mDeviceListAdapter;
    }

    boolean isSearching() {
        return mSearch != null && mSearch.isSearching();
    }

    void clearStandbyDeviceList() {
        mSearch.clearStandbyDeviceList();
    }

}

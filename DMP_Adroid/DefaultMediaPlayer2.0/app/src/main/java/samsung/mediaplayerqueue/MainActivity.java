package samsung.mediaplayerqueue;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.multiscreen.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity
        extends AppCompatActivity
        implements CastStateObserver,
        ConnectStateObserver {

    private static String TAG = "MainActivity";

    private TVSearch mTVSearch = null;
    private Service mService = null;

    private Menu mMenu = null;
    private boolean queueStatus = false;
    private boolean isAppRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreateCalled.");

        //ViewPager..
        ViewPager viewPager;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        //set queue adapter..
        setQueueAdapter();

        //Register CastStateMachine Observer..
        CastStateMachineSingleton.getInstance().registerObserver(this);

        //Register ConnectStateMachine Observer..
        ConnectStateMachineSingleton.getInstance().registerObserver(this);

        //Initialize Playback Controls..
        PlaybackControls.getInstance(this).init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MediaLauncherSingleton.getInstance().disconnect();
        //CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
        CastStateMachineSingleton.getInstance().removeObserver(this);
        ConnectStateMachineSingleton.getInstance().removeObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_connectTV) {
            if(CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {
                displayConnectedDeviceInfo();
            } else if(CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.IDLE){
                //Show Dialog to display TV List.
                populateDeviceList();
            }
            return true;
        } else if(id == R.id.action_queue) {
            if(CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {
                //display queue list
                displayQueueList();
            }
        } else if(id == R.id.action_AddAllToList) {
            if(CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {
                //send whole list to TV
                AddAllToList();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle cast icon changes as per change in device connection status.
     * @param currentCastState current Cast state
     */
    @Override
    public void onCastStatusChange(CastStates currentCastState) {
        if(mMenu != null) {
            MenuItem menuItem = (MenuItem) mMenu.findItem(R.id.action_connectTV);
            if(menuItem != null) {
                if (currentCastState == CastStates.IDLE) {
                    menuItem.setIcon(MainActivity.this.getResources().getDrawable(R.drawable.cast_white_idle));
                    //Just in case we did not get setOnDisconnect()..
                    ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                } else if (currentCastState == CastStates.CONNECTING) {
                    AnimationDrawable castButtonAnimation = (AnimationDrawable) MainActivity.this.getResources().getDrawable(R.drawable.casting_menu_item_animation);
                    menuItem.setIcon(castButtonAnimation);
                    if(castButtonAnimation != null) {
                        castButtonAnimation.start();
                    }
                } else if (currentCastState == CastStates.CONNECTED) {
                    menuItem.setIcon(MainActivity.this.getResources().getDrawable(R.drawable.cast_blue_connected));
                }
            }
        }
    }

    /**
     * Handle AddAllToList & viewList icons on Action Bar.
     * @param currentState current Connect state
     */
    @Override
    public void onConnectStatusChange(ConnectStates currentState) {
        if(mMenu != null) {
            MenuItem menuItem = (MenuItem) mMenu.findItem(R.id.action_connectTV);
            if(menuItem != null) {
                if (currentState == ConnectStates.DISCONNECTED) {
                    mMenu.findItem(R.id.action_queue).setVisible(false);
                    mMenu.findItem(R.id.action_AddAllToList).setVisible(false);
                    //clear the queue if status changes to DISCONNECTED.
                    queueStatus = true;
                    QueueSingleton.getInstance(MainActivity.this).onClearQueue();
                    PlaybackControls.getInstance(this.getApplicationContext()).resetPlaybackControls();
                    displayQueueList();
                    Loader.getInstance(this).destroy();
                } else if (currentState == ConnectStates.CONNECTED) {
                    mMenu.findItem(R.id.action_queue).setVisible(true);
                    mMenu.findItem(R.id.action_AddAllToList).setVisible(true);
                }
            }
        }
    }

    private void displayQueueList() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ListView queueListView = (ListView) findViewById(R.id.queueListView);
        if(!queueStatus) {
            viewPager.setVisibility(View.INVISIBLE);
            queueListView.setVisibility(View.VISIBLE);

            mMenu.findItem(R.id.action_queue).setIcon(R.drawable.queue_active);
            MediaLauncherSingleton.getInstance().fetchQueue();
            queueStatus = true;
        } else {
            viewPager.setVisibility(View.VISIBLE);
            queueListView.setVisibility(View.INVISIBLE);

            mMenu.findItem(R.id.action_queue).setIcon(R.drawable.queue_deactive);
            queueStatus = false;
        }
    }

    private String AssetJSONFile(String filename, Context context) {
        try {
            AssetManager manager = context.getAssets();
            InputStream is = manager.open(filename);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void AddAllToList() {
        // Find current TAB..
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        int currentItem = viewPager.getCurrentItem();

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> item = null;

        switch(currentItem) {
            case 0: {   //Video
                try {
                    JSONObject obj = new JSONObject(AssetJSONFile("videolist.json", MainActivity.this));
                    JSONArray jarray = obj.getJSONArray("videos");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json = jarray.getJSONObject(i);
                        item = new HashMap<String, String>();
                        item.put(MediaLauncherSingleton.URL, json.getString("url"));
                        item.put(MediaLauncherSingleton.TITLE, json.getString("title"));
                        item.put(MediaLauncherSingleton.VIDEO_THUMBNAIL_URL, json.getString("thumbUrl"));
                        list.add(item);
                    }
                } catch(Exception e){
                    Log.d(TAG, "Error: " + e);
                }
                MediaLauncherSingleton.getInstance().enqueue(list, MediaLauncherSingleton.PlayerType.VIDEO);
                break;
            }
            case 1: {   //Audio
                try {
                    JSONObject obj = new JSONObject(AssetJSONFile("audiolist.json", MainActivity.this));
                    JSONArray jarray = obj.getJSONArray("audios");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json = jarray.getJSONObject(i);
                        item = new HashMap<String, String>();
                        item.put(MediaLauncherSingleton.URL, json.getString("url"));
                        item.put(MediaLauncherSingleton.TITLE, json.getString("title"));
                        item.put(MediaLauncherSingleton.AUDIO_ALBUM_NAME, json.getString("albumName"));
                        item.put(MediaLauncherSingleton.AUDIO_ALBUM_ART, json.getString("albumArt"));
                        list.add(item);
                    }
                } catch(Exception e){
                    Log.d(TAG, "Error: " + e);
                }
                MediaLauncherSingleton.getInstance().enqueue(list, MediaLauncherSingleton.PlayerType.AUDIO);
                break;
            }
            case 2: {   //Photo
                try {
                    JSONObject obj = new JSONObject(AssetJSONFile("photolist.json", MainActivity.this));
                    JSONArray jarray = obj.getJSONArray("photos");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json = jarray.getJSONObject(i);
                        item = new HashMap<String, String>();
                        item.put(MediaLauncherSingleton.URL, json.getString("url"));
                        item.put(MediaLauncherSingleton.TITLE, json.getString("title"));
                        list.add(item);
                    }
                } catch(Exception e){
                    Log.d(TAG, "Error: " + e);
                }
                MediaLauncherSingleton.getInstance().enqueue(list, MediaLauncherSingleton.PlayerType.PHOTO);
                break;
            }
        }

    }

    private void setQueueAdapter() {
        ListView queue = (ListView)findViewById(R.id.queueListView);
        if (null == queue) {
            Log.e(TAG, "Empty queueList!!!");
            return;
        }
        queue.setAdapter(QueueSingleton.getInstance(MainActivity.this).getQueueAdapter());
    }

    /*
     * Check network connectivity..
     */
    private boolean checkNetworkConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        } else
        {
            return false;
        }
    }

    /*
     * Method to display connected device information & show "Disconnect" button.
     */
    private void displayConnectedDeviceInfo() {
        final Dialog lstDialog = new Dialog(this);
        lstDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater serviceList = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = serviceList.inflate(R.layout.layout_disconnect_tv, null, false);
        lstDialog.setContentView(view);
        lstDialog.setCancelable(true);

        if(mService != null) {
            TextView connectedTVName = (TextView)lstDialog.findViewById(R.id.txtConnectedTVName);
            connectedTVName.setText(mService.getName());
        }
        lstDialog.show();

        Button btnDisconnectTV = (Button) lstDialog.findViewById(R.id.btnDisconnect);
        btnDisconnectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaLauncherSingleton.getInstance().disconnect();
                CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                lstDialog.dismiss();
            }
        });
    }

    /**
     * [DISCOVERY] Populate Connected TV List..
     */
    private void populateDeviceList() {
        if(!checkNetworkConnectivity()) {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
        } else {
            /*Prepare Dialog box..*/
            final Dialog lstDialog = new Dialog(this);
            lstDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater serviceList = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = serviceList.inflate(R.layout.layout_tvlist, null, false);
            lstDialog.setContentView(view);
            lstDialog.setCancelable(true);

            /*Animate cast icon in dialog box*/
            ImageView castingIcon = (ImageView)lstDialog.findViewById(R.id.imgCastIcon);
            AnimationDrawable castButtonAnimation = (AnimationDrawable) MainActivity.this.getResources().getDrawable(R.drawable.casting_icon_animation);
            castingIcon.setBackground(castButtonAnimation);
            if(castButtonAnimation != null) {
                castButtonAnimation.start();
            }

            mTVSearch = new TVSearch(MainActivity.this);

            //fill tvList..
            final ListView lstConnectedTv = (ListView) lstDialog.findViewById(R.id.tvList);
            lstConnectedTv.setAdapter(mTVSearch.getTVListAdapter());

            /*start discovery..*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mTVSearch.startDiscovery();
                }
            }).start();

            //display dialog (list view)..
            lstDialog.show();

            //set cast state as Connecting..
            CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.CONNECTING);

            lstConnectedTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*Get selected TV's service object*/
                    mService = (Service) parent.getItemAtPosition(position);
                    /*Dismiss TV List Dialog*/
                    lstDialog.dismiss();
                    /*Set service for the app*/
                    MediaLauncherSingleton.getInstance().setService(MainActivity.this, mService);

                    /*Stop discovery*/
                    mTVSearch.stopDiscovery();
                }
            });

            lstDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mTVSearch.isSearching()) {
                        mTVSearch.stopDiscovery();
                    }
                    if (CastStateMachineSingleton.getInstance().getCurrentCastState() != CastStates.CONNECTED) {
                        CastStateMachineSingleton.getInstance().setCurrentCastState(CastStates.IDLE);
                    }
                }
            });
        }
    }

    /*
     * Setting up ViewPager (fragments) on MainAcitivty..
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VideosFragment(), "Videos");
        adapter.addFragment(new AudiosFragment(), "Audios");
        adapter.addFragment(new PhotosFragment(), "Photos");
        viewPager.setAdapter(adapter);
        int fragCount = adapter.getCount();
        int selectedFrag = fragCount / 2;
        viewPager.setCurrentItem(selectedFrag);
    }

    /*
     * Adapter class to add fragments.
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

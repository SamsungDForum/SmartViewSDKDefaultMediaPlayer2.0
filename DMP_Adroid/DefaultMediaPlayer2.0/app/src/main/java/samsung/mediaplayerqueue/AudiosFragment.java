package samsung.mediaplayerqueue;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Ankit Saini
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudiosFragment extends Fragment{
    public static final String TAG = "AudiosFragment";
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private AudiosListViewAdapter listAdapter;

    public static AudiosFragment newInstance(String param1, String param2) {
        AudiosFragment fragment = new AudiosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AudiosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == container) {
            Log.w(TAG, "NULL container!");
            return null;
        }
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_audios, container, false);
        listAdapter = new AudiosListViewAdapter(container.getContext(),
                R.layout.layout_audios,
                getData());

        if (0 == listAdapter.getCount()) {
            Log.w(TAG, "No Audios Found!");
            return view;
        }

        listView = (ListView) view.findViewById(R.id.audiosListView);
        if (null == listView) {
            Log.w(TAG, "Empty listView!!!");
            return view;
        }

        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        // On Click Item Listener implementation for audios ListView..
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {

                    // Get position of the clicked image..
                    final AudioItem item = (AudioItem) parent.getItemAtPosition(position);

                    // Send this url to TV..
                    MediaLauncherSingleton.getInstance().playContent(item.audioUrl,
                            item.audioTitle,
                            item.albumName,
                            item.albumArt);
                } else {
                    Toast.makeText(getActivity(), "Please connect to a TV.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
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

    // Prepare data for listview..
    private ArrayList<AudioItem> getData() {
        final ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("audiolist.json", getActivity()));
            JSONArray jarray = obj.getJSONArray("audios");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject json = jarray.getJSONObject(i);
                audioItems.add(new AudioItem(
                        json.getString("url"),
                        json.getString("title"),
                        json.getString("albumName"),
                        json.getString("albumArt")));
            }
        } catch(Exception e){
            Log.d(TAG, "Exception in parsing json file: " + e);
        }
        return audioItems;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}

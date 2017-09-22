package samsung.mediaplayerqueue;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
 * {@link PhotosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment{
    private static final String TAG = "PhotosFragment";
    private OnFragmentInteractionListener mListener;

    public static PhotosFragment newInstance(String param1, String param2) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotosFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_photos, container, false);
        GridView gridView;
        PhotosListViewAdapter listAdapter;

        listAdapter = new PhotosListViewAdapter(container.getContext(),
                R.layout.layout_photos,
                getData());

        if (0 == listAdapter.getCount()) {
            Log.w(TAG, "No Photos Found!");
            return view;
        }

        gridView = (GridView) view.findViewById(R.id.photosGridView);
        if (null == gridView) {
            Log.w(TAG, "Empty gridView!!!");
            return view;
        }

        gridView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        // Set gridView column width dynamically..
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int columnLength = screenWidth / 3;

        gridView.setColumnWidth(columnLength);

        // On Click Item Listener implementation for photos ListView..
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CastStateMachineSingleton.getInstance().getCurrentCastState() == CastStates.CONNECTED) {

                    // Get position of the clicked image..
                    final PhotoItem item = (PhotoItem) parent.getItemAtPosition(position);

                    // Send this url to TV..
                    MediaLauncherSingleton.getInstance(getContext()).playContent(item.photoUrl,
                            item.photoTitle);
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
            if(is.read(buffer) > 0) {
                is.close();
            }

            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Prepare data for listview..
    private ArrayList<PhotoItem> getData() {
        final ArrayList<PhotoItem> photoItems = new ArrayList<PhotoItem>();
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("photolist.json", getActivity()));
            JSONArray jarray = obj.getJSONArray("photos");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject json = jarray.getJSONObject(i);
                photoItems.add(new PhotoItem(
                        json.getString("url"),
                        json.getString("title")));
            }
        } catch(Exception e){
            Log.d(TAG, "Exception in parsing json file: " + e);
        }
        return photoItems;
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

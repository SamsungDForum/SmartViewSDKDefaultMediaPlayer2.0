package samsung.mediaplayerqueue;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

/**
 * @author Ankit Saini
 * This singleton class is responsible for displaying the loader bar when an event is in progress.
 */
public class Loader {
    public static final String TAG = "Loader";

    private static Loader mInstance = null;
    private static Context mContext;
    private static Dialog mDialog = null;
    private static boolean loaderOnDisplay = false;

    private Loader() {}

    private void destroyTimer(long timeout) {
        new CountDownTimer(timeout*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                if(loaderOnDisplay) {
                    Toast.makeText(mContext, "TV is taking too long to process your request.\nExit TV Application & start again.", Toast.LENGTH_SHORT).show();
                    destroy();
                }
            }
        }.start();
    }

    public static Loader getInstance(Context context) {
        mContext = context;
        if(mInstance == null) {
            mInstance = new Loader();
        }
            return mInstance;
    }

    public void display() {
        Log.d(TAG, "display()");
        if(mDialog == null) {
            loaderOnDisplay = true;
            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.layout_loader, null, false);
            mDialog.setContentView(dialogView);
            mDialog.setCancelable(false);
            mDialog.show();
            //if there's no reply from TV side - destroy the loader after some time.
            destroyTimer(30);
        }
    }

    public void destroy() {
        Log.d(TAG, "destroy()");
        if(mDialog != null
                && mDialog.isShowing()
                && loaderOnDisplay) {
            Log.d(TAG, "destroyed!");
            mDialog.dismiss();
            mDialog = null;
            loaderOnDisplay = false;
        }

    }
}

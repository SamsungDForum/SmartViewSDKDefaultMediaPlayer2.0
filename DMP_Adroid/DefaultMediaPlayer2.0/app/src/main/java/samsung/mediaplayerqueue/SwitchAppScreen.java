package samsung.mediaplayerqueue;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * @author Ankit Saini
 * This singleton class is responsible for displaying the switch message when TV widget is in background.
 */
public class SwitchAppScreen {
    public static final String TAG = "SwitchAppScreen";

    private static SwitchAppScreen mInstance = null;
    private static Context mContext;
    private static Dialog mDialog = null;

    private SwitchAppScreen() {}

    public static SwitchAppScreen getInstance(Context context) {
        mContext = context;
        if(mInstance == null) {
            mInstance = new SwitchAppScreen();
        }
        return mInstance;
    }

    public void display() {
        Log.d(TAG, "display()");
        if(mDialog == null) {
            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.layout_switch_app_screen, null, false);
            mDialog.setContentView(dialogView);
            mDialog.setCancelable(false);

            Button btnBringAppToForeground = (Button) mDialog.findViewById(R.id.btnBringAppToForeground);
            if(btnBringAppToForeground != null) {
                btnBringAppToForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MediaLauncherSingleton.getInstance().resumeApplicationInForeground();
                    }
                });
                mDialog.show();
            }
        }
    }

    public void destroy() {
        Log.d(TAG, "destroy()");
        if(mDialog != null && mDialog.isShowing()) {
            Log.d(TAG, "destroyed!");
            mDialog.dismiss();
            mDialog = null;
        }

    }
}

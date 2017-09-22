package samsung.mediaplayerqueue;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ankit Saini
 * This singleton class is responsible for displaying the switch message when TV widget is in background.
 */
class SwitchAppScreen
        extends View {
    private static final String TAG = "SwitchAppScreen";

    private static SwitchAppScreen mInstance = null;
    private static Dialog mDialog = null;

    private final ReentrantLock rLock = new ReentrantLock();

    private SwitchAppScreen(Context context) {
        super(context);
    }
    private SwitchAppScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    static SwitchAppScreen getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SwitchAppScreen(context);
        }
        return mInstance;
    }

    void display() {
        Log.d(TAG, "display()");
        if(mDialog == null) {
            rLock.lock();
            try {
                mDialog = new Dialog(getContext());
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = layoutInflater.inflate(R.layout.layout_switch_app_screen, null, false);
                mDialog.setContentView(dialogView);
                mDialog.setCancelable(false);

                Button btnBringAppToForeground = (Button) mDialog.findViewById(R.id.btnBringAppToForeground);
                if (btnBringAppToForeground != null) {
                    btnBringAppToForeground.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MediaLauncherSingleton.getInstance(getContext()).resumeApplicationInForeground();
                        }
                    });
                    mDialog.show();
                }
            } finally {
                rLock.unlock();
            }
        }
    }

    void destroy() {
        Log.d(TAG, "destroy()");
        if(mDialog != null && mDialog.isShowing()) {
            Log.d(TAG, "destroyed!");
            mDialog.dismiss();
            mDialog = null;
        }

    }
}

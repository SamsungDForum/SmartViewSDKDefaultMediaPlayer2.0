package samsung.mediaplayerqueue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @author Ankit Saini
 * Manages settings page of the app.
 */

class Settings extends View {
    private static String TAG = "Settings";

    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private Dialog mSettingsDialog = null;
    private static Settings mInstance = null;

    static Settings getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new Settings(context);
        }
        return mInstance;
    }

    private Settings(Context context) {
        super(context);
        mSharedPreferences = context.getSharedPreferences(
                getContext().getString(R.string.sharedPrefrencesFile),
                Context.MODE_PRIVATE);

        //forcing defaults..
        String watermarkUrl = getString(getContext().getResources().getString(R.string.watermarkUrl));
        String backgroundMusic = getString(getContext().getResources().getString(R.string.bgAudioUrl));
        if(watermarkUrl == null) {
            putString(getContext().getResources().getString(R.string.watermarkUrl), "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Samsung_Logo.svg/2000px-Samsung_Logo.svg.png");
        }
        if(backgroundMusic == null) {
            putString(getContext().getResources().getString(R.string.bgAudioUrl), "https://developer.samsung.com/onlinedocs/tv/SmartView/sample/audio/Ketsa_-_11_-_Retake.mp3");
        }
    }

    private boolean putInt(String key, int value) {
        mEditor = mSharedPreferences.edit();
        if(key == null) {
            Log.e(TAG, "putInt(): 'key' is NULL.");
            return false;
        }

        if(value < 0) {
            Log.e(TAG, "putInt(): Negative value.");
        }
        mEditor.putInt(key, value);
        mEditor.apply();
        return true;
    }

    private boolean putString(String key, String value) {
        mEditor = mSharedPreferences.edit();
        if(key == null) {
            Log.e(TAG, "putInt(): 'key' is NULL.");
            return false;
        }
        if(value == null) {
            Log.e(TAG, "putInt(): 'value' is NULL.");
            return false;
        }
        mEditor.putString(key, value);
        mEditor.apply();
        return true;
    }

    private boolean putBool(String key, Boolean value) {
        mEditor = mSharedPreferences.edit();
        if(key == null) {
            Log.e(TAG, "putInt(): 'key' is NULL.");
            return false;
        }

        mEditor.putBoolean(key, value);
        mEditor.apply();
        return true;
    }

    int getInt(String key) {
        return mSharedPreferences.getInt(key, -1/*default value*/);
    }

    String getString(String key) {
        return mSharedPreferences.getString(key, null/*default value*/);
    }

    Boolean getBool(String key) {
        return mSharedPreferences.getBoolean(key, false/*default value*/);
    }

    void hide() {
        if(mSettingsDialog != null) {
            mSettingsDialog.dismiss();
        }
    }

    void show() {
        LayoutInflater settingsList = ((Activity)getContext()).getWindow().getLayoutInflater();
        View view = settingsList.inflate(R.layout.layout_settings, null, false);
        mSettingsDialog = new Dialog(getContext());
        mSettingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSettingsDialog.setContentView(view);
        mSettingsDialog.setCancelable(true);

        //Read from settings file & fill the settings fields.
        String url1 = getString(getContext().getResources().getString(R.string.bgImageUrl1));
        String url2 = getString(getContext().getResources().getString(R.string.bgImageUrl2));
        String url3 = getString(getContext().getResources().getString(R.string.bgImageUrl3));
        String watermarkUrl = getString(getContext().getResources().getString(R.string.watermarkUrl));
        String backgroundMusic = getString(getContext().getResources().getString(R.string.bgAudioUrl));
        Boolean showStandbyDevices = getBool(getContext().getResources().getString(R.string.showStandbyDevices));
        Boolean closeOnDisconnect = getBool(getContext().getResources().getString(R.string.closeOnDisconnect));
        Boolean showStandbyScreen = getBool(getContext().getResources().getString(R.string.showStandbyScreen));
        Boolean firstDBInitialization = getBool(getContext().getResources().getString(R.string.firstDBInitialize));

        // Set defaults if it's a 1st time launch..
        if(!firstDBInitialization) {
            showStandbyDevices = true;
            closeOnDisconnect = true;
            showStandbyScreen = false;

            putBool(getContext().getResources().getString(R.string.firstDBInitialize), true);
        }

        ((EditText) mSettingsDialog.findViewById(R.id.txtImgUrl1)).setText(url1 != null ? url1 : "");
        ((EditText)mSettingsDialog.findViewById(R.id.txtImgUrl2)).setText(url2 != null ? url2 : "");
        ((EditText)mSettingsDialog.findViewById(R.id.txtImgUrl3)).setText(url3 != null ? url3 : "");
        ((EditText)mSettingsDialog.findViewById(R.id.txtWatermarkUrl)).setText(watermarkUrl);
        ((EditText)mSettingsDialog.findViewById(R.id.txtBackgroundMusic)).setText(backgroundMusic);
        ((ToggleButton)mSettingsDialog.findViewById(R.id.tglShowStandbyDevices)).setChecked(showStandbyDevices);
        ((ToggleButton)mSettingsDialog.findViewById(R.id.tglOnDisconnect)).setChecked(closeOnDisconnect);


        //Set visibility of Clear Standby Devices button..
        final RelativeLayout rlvClearStandbyTVs = (RelativeLayout)mSettingsDialog.findViewById(R.id.rlvClearStandbyTVs);
        if(showStandbyDevices) {
            rlvClearStandbyTVs.setVisibility(View.VISIBLE);
        } else {
            rlvClearStandbyTVs.setVisibility(View.GONE);
        }

        //Toggle Standby screen settings as per previous settings.
        ToggleButton tglShowStandbyScreen = (ToggleButton)mSettingsDialog.findViewById(R.id.tglShowStandbyScreen);
        tglShowStandbyScreen.setChecked(showStandbyScreen);
        if(tglShowStandbyScreen.isChecked()) {
            mSettingsDialog.findViewById(R.id.rlvBgImagesList).setVisibility(View.VISIBLE);
        }

        mSettingsDialog.show();

        //Set listeners..
        ToggleButton tglShowStandbyDevices = (ToggleButton)mSettingsDialog.findViewById(R.id.tglShowStandbyDevices);
        tglShowStandbyDevices.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rlvClearStandbyTVs.setVisibility(View.VISIBLE);
                } else {
                    rlvClearStandbyTVs.setVisibility(View.GONE);
                }
            }
        });

        final Button btnClearStandbyDevices = (Button)mSettingsDialog.findViewById(R.id.btnClearStandbyTVs);
        btnClearStandbyDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Long press to clear standby devices list.", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearStandbyDevices.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TVSearch.getInstance(getContext()).clearStandbyDeviceList();
                Toast.makeText(getContext(), "Standby devices list successfully cleared.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tglShowStandbyScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSettingsDialog == null) {
                    return;
                }

                RelativeLayout relativeLayout = (RelativeLayout)mSettingsDialog.findViewById(R.id.rlvBgImagesList);
                if(isChecked) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    putBool(getContext().getResources().getString(R.string.showStandbyScreen), true);
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    putBool(getContext().getResources().getString(R.string.showStandbyScreen), false);
                }
            }
        });

        //Save settings once user presses BACK button..
        mSettingsDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    saveSettings();
                    mInstance = null;
                }
                return true;
            }
        });

        //Save settings once user cancels dialog..
        mSettingsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                saveSettings();
                mInstance = null;
            }
        });
    }

    private void saveSettings() {
        String bgImageUrl1;
        String bgImageUrl2;
        String bgImageUrl3;
        String watermarkUrl;

        String bgAudioUrl;
        Boolean showStandbyDevices;
        Boolean closeOnDisconnect;

        bgImageUrl1 = ((EditText) mSettingsDialog.findViewById(R.id.txtImgUrl1)).getText().toString();
        bgImageUrl2 = ((EditText) mSettingsDialog.findViewById(R.id.txtImgUrl2)).getText().toString();
        bgImageUrl3 = ((EditText) mSettingsDialog.findViewById(R.id.txtImgUrl3)).getText().toString();
        watermarkUrl = ((EditText) mSettingsDialog.findViewById(R.id.txtWatermarkUrl)).getText().toString();
        putString(getContext().getResources().getString(R.string.bgImageUrl1), bgImageUrl1);
        putString(getContext().getResources().getString(R.string.bgImageUrl2), bgImageUrl2);
        putString(getContext().getResources().getString(R.string.bgImageUrl3), bgImageUrl3);
        putString(getContext().getResources().getString(R.string.watermarkUrl), watermarkUrl);
        

        bgAudioUrl = ((EditText) mSettingsDialog.findViewById(R.id.txtBackgroundMusic)).getText().toString();
        putString(getContext().getResources().getString(R.string.bgAudioUrl), bgAudioUrl);
        showStandbyDevices = ((ToggleButton) mSettingsDialog.findViewById(R.id.tglShowStandbyDevices)).isChecked();
        putBool(getContext().getResources().getString(R.string.showStandbyDevices), showStandbyDevices);
        closeOnDisconnect = ((ToggleButton) mSettingsDialog.findViewById(R.id.tglOnDisconnect)).isChecked();
        putBool(getContext().getResources().getString(R.string.closeOnDisconnect), closeOnDisconnect);
        mSettingsDialog.dismiss();
    }
}

package samsung.mediaplayerqueue;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ankit Saini
 * Class to maintain the connect satus of the application.
 */
class ConnectStateMachineSingleton implements ConnectStateHandler{
    private final String TAG = "ConnectStateM.Singleton";
    private ConnectStates mCurrentConnectState;
    private static ConnectStateMachineSingleton mInstance = null;
    private List<ConnectStateObserver> listners = new ArrayList<ConnectStateObserver>();

    private void initConnectStateMachine() {
        mCurrentConnectState = ConnectStates.DISCONNECTED;
    }

    private ConnectStateMachineSingleton() {
        initConnectStateMachine();
    }

    static ConnectStateMachineSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new ConnectStateMachineSingleton();
        }
        return mInstance;
    }

    void setCurrentConnectState(ConnectStates currentConnectState) {
        Log.d(TAG, "Connection status changed to: " + currentConnectState.name());
        this.mCurrentConnectState = currentConnectState;
        connectStatusChangeObserver(currentConnectState);
    }

    ConnectStates getCurrentConnectState() {
        return this.mCurrentConnectState;
    }

    /*
     * Implementing Observer class..
     */
    @Override
    public void registerObserver(ConnectStateObserver observer) {
        Log.v(TAG, "Observer Registered: " + observer.toString());
        listners.add(observer);
    }

    @Override
    public void removeObserver(ConnectStateObserver observer) {
        Log.v(TAG, "Observer Un-registered: " + observer.toString());
        listners.remove(observer);
    }

    @Override
    public void connectStatusChangeObserver(ConnectStates currentState) {
        for (ConnectStateObserver observer : listners) {
            observer.onConnectStatusChange(currentState);
        }
    }
}

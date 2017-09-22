package samsung.mediaplayerqueue;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ankit Saini
 * Class to maintain the cast satus of the application.
 */
class CastStateMachineSingleton implements CastStateHandler {
    private final String TAG = "CastStateM.Singleton";
    private CastStates mCurrentCastState;
    private static CastStateMachineSingleton mInstance = null;
    private List<CastStateObserver> listners = new ArrayList<CastStateObserver>();

    private void initCastStateMachine() {
        mCurrentCastState = CastStates.IDLE;
    }

    private CastStateMachineSingleton() {
        initCastStateMachine();
    }

    static CastStateMachineSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new CastStateMachineSingleton();
        }
        return mInstance;
    }

    public void setCurrentCastState(CastStates currentCastState) {
        Log.d(TAG, "Cast Satatus Changed to: " + currentCastState.name());
        this.mCurrentCastState = currentCastState;
        castStatusChangeObserver(currentCastState);
    }

    public CastStates getCurrentCastState() {
        return this.mCurrentCastState;
    }

    /*
     * Implementing Observer class..
     */
    @Override
    public void registerObserver(CastStateObserver observer) {
        Log.v(TAG, "Observer Registered: " + observer.toString());
        listners.add(observer);
    }

    @Override
    public void removeObserver(CastStateObserver observer) {
        Log.v(TAG, "Observer Un-registered: " + observer.toString());
        listners.remove(observer);
    }

    @Override
    public void castStatusChangeObserver(CastStates currentState) {
        for (CastStateObserver observer : listners) {
            observer.onCastStatusChange(currentState);
        }
    }
}

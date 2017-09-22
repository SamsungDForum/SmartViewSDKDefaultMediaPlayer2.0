package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for cast status change notification handler.
 */
interface CastStateObserver {
    void onCastStatusChange(CastStates value);
}

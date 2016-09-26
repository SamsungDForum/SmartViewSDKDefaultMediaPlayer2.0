package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for cast status change notification handler.
 */
public interface CastStateObserver {
    public void onCastStatusChange(CastStates value);
}

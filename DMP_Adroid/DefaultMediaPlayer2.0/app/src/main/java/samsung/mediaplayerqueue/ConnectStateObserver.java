package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for connect status change notification handler.
 */
public interface ConnectStateObserver {
    public void onConnectStatusChange(ConnectStates value);
}

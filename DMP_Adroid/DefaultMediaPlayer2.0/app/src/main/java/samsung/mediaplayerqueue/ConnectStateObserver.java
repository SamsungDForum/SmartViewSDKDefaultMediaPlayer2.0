package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for connect status change notification handler.
 */
interface ConnectStateObserver {
    void onConnectStatusChange(ConnectStates value);
}

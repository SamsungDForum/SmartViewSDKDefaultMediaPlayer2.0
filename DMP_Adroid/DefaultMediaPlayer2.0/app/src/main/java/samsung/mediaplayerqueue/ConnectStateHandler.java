package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for registering & deregistering to connected status notifications.
 */
interface ConnectStateHandler {
    void registerObserver(ConnectStateObserver observer);
    void removeObserver(ConnectStateObserver observer);

    void connectStatusChangeObserver(ConnectStates value);
}

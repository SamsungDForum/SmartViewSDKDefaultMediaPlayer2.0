package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for registering & deregistering to connected status notifications.
 */
public interface ConnectStateHandler {
    public void registerObserver(ConnectStateObserver observer);
    public void removeObserver(ConnectStateObserver observer);

    public void connectStatusChangeObserver(ConnectStates value);
}

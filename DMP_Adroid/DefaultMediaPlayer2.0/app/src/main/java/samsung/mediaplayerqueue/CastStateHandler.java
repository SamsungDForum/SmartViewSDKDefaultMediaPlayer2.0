package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for registering & deregistering to Cast status notifications.
 */
public interface CastStateHandler {
    public void registerObserver(CastStateObserver observer);
    public void removeObserver(CastStateObserver observer);

    public void castStatusChangeObserver(CastStates value);
}

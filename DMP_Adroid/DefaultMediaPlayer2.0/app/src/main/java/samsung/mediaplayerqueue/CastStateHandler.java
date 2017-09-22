package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Interface class for registering & deregistering to Cast status notifications.
 */
interface CastStateHandler {
    void registerObserver(CastStateObserver observer);
    void removeObserver(CastStateObserver observer);

    void castStatusChangeObserver(CastStates value);
}

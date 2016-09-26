package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage AudioItems
 */
public class AudioItem {
    public String audioUrl;
    public String audioTitle;
    public String albumName;
    public String albumArt;

    public AudioItem(
            String audioUrl,
            String audioTitle,
            String albumName,
            String albumArt) {
        this.audioUrl = audioUrl;
        this.audioTitle = audioTitle;
        this.albumName = albumName;
        this.albumArt = albumArt;
    }

}

package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage AudioItems
 */
class AudioItem {
    String audioUrl;
    String audioTitle;
    String albumName;
    String albumArt;

    AudioItem(
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

package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage VideoItems
 */
public class VideoItem {
    public String videoUrl;
    public String videoTitle;
    public String thumbnailUrl;

    public VideoItem(
                     String videoUrl,
                     String videoTitle,
                     String thumbnailUrl) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.thumbnailUrl = thumbnailUrl;
    }

}

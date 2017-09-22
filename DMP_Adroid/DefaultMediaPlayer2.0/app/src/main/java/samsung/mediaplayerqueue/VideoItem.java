package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage VideoItems
 */
class VideoItem {
    String videoUrl;
    String videoTitle;
    String thumbnailUrl;

    VideoItem(
                     String videoUrl,
                     String videoTitle,
                     String thumbnailUrl) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.thumbnailUrl = thumbnailUrl;
    }

}

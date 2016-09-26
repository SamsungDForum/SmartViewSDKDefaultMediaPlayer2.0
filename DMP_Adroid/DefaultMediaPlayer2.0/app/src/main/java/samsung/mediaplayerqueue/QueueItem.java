package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage Queue Items
 */
public class QueueItem {
    public String contentUrl;
    public String contentTitle;
    public String contentthumbUrl;

    public QueueItem(
            String contentUrl,
            String contentTitle,
            String contentthumbUrl) {
        this.contentUrl = contentUrl;
        this.contentTitle = contentTitle;
        this.contentthumbUrl = contentthumbUrl;
    }

}
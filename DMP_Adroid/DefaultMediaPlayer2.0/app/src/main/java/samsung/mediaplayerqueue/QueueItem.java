package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage Queue Items
 */
class QueueItem {
    String contentUrl;
    String contentTitle;
    String contentthumbUrl;

    QueueItem(
            String contentUrl,
            String contentTitle,
            String contentthumbUrl) {
        this.contentUrl = contentUrl;
        this.contentTitle = contentTitle;
        this.contentthumbUrl = contentthumbUrl;
    }

}
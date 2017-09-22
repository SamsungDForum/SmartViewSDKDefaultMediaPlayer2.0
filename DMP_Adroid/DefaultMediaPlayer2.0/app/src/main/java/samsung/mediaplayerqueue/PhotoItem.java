package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage PhotoItems
 */
class PhotoItem {
    String photoUrl;
    String photoTitle;

    PhotoItem (
            String photoUrl,
            String photoTitle) {
        this.photoUrl = photoUrl;
        this.photoTitle = photoTitle;
    }
}

package samsung.mediaplayerqueue;

/**
 * @author Ankit Saini
 * Class to manage PhotoItems
 */
public class PhotoItem {
    public String photoUrl;
    public String photoTitle;

    public PhotoItem (
            String photoUrl,
            String photoTitle) {
        this.photoUrl = photoUrl;
        this.photoTitle = photoTitle;
    }
}

package db.dataSets;

/**
 * Created by iters on 3/16/17.
 */
public class PhotoPost {
    public int mediaId, albumId, ownerId;
    public String url;

    public PhotoPost(int mediaId, int albumId, int ownerId, String url) {
        this.mediaId = mediaId;
        this.albumId = albumId;
        this.ownerId = ownerId;
        this.url = url;
    }
}

package db.dataSets;

import java.util.List;

/**
 * Created by iters on 3/16/17.
 */
public class ParsedPost {
    public int niche, id, likes, comments, reposts, fromPub;
    List<PhotoPost> ph;
    // Date

    public void setUp(int likes, int reposts, int comments) {
        this.likes = likes;
        this.reposts = reposts;
        this.comments = comments;
    }
}

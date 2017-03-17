package db.dataSets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iters on 3/16/17.
 */
public class ParsedPost {
    public int niche, id, likes, comments, reposts, fromPub;
    public List<PhotoPost> ph;
    public String text;
    // Date

    public ParsedPost() {
        ph = new ArrayList<>();
    }

    public void setUp(int likes, int reposts, int comments) {
        this.likes = likes;
        this.reposts = reposts;
        this.comments = comments;
    }
}

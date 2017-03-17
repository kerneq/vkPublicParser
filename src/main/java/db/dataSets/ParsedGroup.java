package db.dataSets;

/**
 * Created by iters on 3/14/17.
 */
public class ParsedGroup {
    public int id;
    public int pubId;
    public int peopleCount;
    public int niche;
    public int last_post_unix;

    public ParsedGroup(int id, int pubId, int peopleCount, int niche, int last_post_unix) {
        this.id = id;
        this.pubId = pubId;
        this.peopleCount = peopleCount;
        this.niche = niche;
        this.last_post_unix = last_post_unix;
    }

    @Override
    public String toString() {
        return "id: " + id  + " public_id: " + pubId + " count: " + peopleCount;
    }
}
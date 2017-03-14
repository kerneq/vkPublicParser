package db.dataSets;

/**
 * Created by iters on 3/14/17.
 */
public class ParsedGroup {
    public int id;
    public int pubId;
    public int peopleCount;

    public ParsedGroup(int id, int pubId, int peopleCount) {
        this.id = id;
        this.pubId = pubId;
        this.peopleCount = peopleCount;
    }

    @Override
    public String toString() {
        return "id: " + id  + " public_id: " + pubId + " count: " + peopleCount;
    }
}
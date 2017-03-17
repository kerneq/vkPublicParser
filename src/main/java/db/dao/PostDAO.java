package db.dao;

import db.dataSets.ParsedPost;
import db.dataSets.PhotoPost;
import java.sql.*;

/**
 * Created by iters on 3/16/17.
 */
public class PostDAO {
    private Connection connection;

    public PostDAO(Connection connection) {
        this.connection = connection;
    }

    public void addPost(ParsedPost post, int niche, int pubId) throws SQLException {
        Statement stm = connection.createStatement();
        String query = String.format("INSERT INTO post_info" +
                "(id," +
                "text," +
                "niche_id," +
                "likes," +
                "reposts," +
                "comments," +
                "donor_id," +
                "parse_date)" +
                "VALUES" +
                "(NULL," +
                "%s" +
                "%d" +
                "%d" +
                "%d" +
                "%d" +
                "%d" +
                "NOW());", post.text, niche, post.likes, post.reposts, post.comments, pubId);
        stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        ResultSet res = stm.getResultSet();

        int newPostId = res.getInt("id");
        for (PhotoPost ph : post.ph) {
            query = String.format("INSERT INTO photo" +
                    "(id," +
                    "media_id," +
                    "album_id," +
                    "owner_id," +
                    "url," +
                    "post_id)" +
                    "VALUES" +
                    "(null," +
                    "%d," +
                    "%d," +
                    "%d," +
                    "%s," +
                    "%d);", ph.mediaId, ph.albumId, ph.ownerId, ph.url, newPostId);
            stm.executeUpdate(query);
        }
        stm.close();
    }
}

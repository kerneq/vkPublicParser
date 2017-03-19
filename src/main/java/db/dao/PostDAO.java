package db.dao;

import db.dataSets.ParsedPost;
import db.dataSets.PhotoPost;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
        if (post.text == null) {
            post.text = "";
        }

        String txt = null;
        try {
            txt = new String(post.text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
                "'%s'," +
                "%d," +
                "%d," +
                "%d," +
                "%d," +
                "%d," +
                "NOW());", txt, niche, post.likes, post.reposts, post.comments, pubId);
        stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

        ResultSet res = stm.getGeneratedKeys();
        res.next();
        int newPostId = res.getInt(1);
        System.out.println("NEW ID : " + newPostId);
        for (PhotoPost ph : post.ph) {
            query = String.format("INSERT INTO photo" +
                    "(id," +
                    "media_id," +
                    "album_id," +
                    "owner_id," +
                    "url," +
                    "post_id) " +
                    "VALUES" +
                    "(NULL," +
                    "%d," +
                    "%d," +
                    "%d," +
                    "'%s'," +
                    "%d);", ph.mediaId, ph.albumId, ph.ownerId, ph.url, newPostId);
            stm.executeUpdate(query);
        }
        stm.close();
    }
}

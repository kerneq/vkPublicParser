package db.dao;

import db.dataSets.ParsedPost;

import java.sql.Connection;

/**
 * Created by iters on 3/16/17.
 */
public class PostDAO {
    private Connection connection;

    public PostDAO(Connection connection) {
        this.connection = connection;
    }

    public void addPost(ParsedPost post, int niche) {

    }
}

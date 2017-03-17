package db.dao;

import db.dataSets.ParsedGroup;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iters on 3/14/17.
 */
public class ParsedGroupDAO {
    private Connection connection;

    public ParsedGroupDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ParsedGroup> getParsedPubList() throws SQLException {
        List<ParsedGroup> groups = new ArrayList<>();
        Statement stm = connection.createStatement();

        String query = "SELECT * FROM parse_from;";
        stm.execute(query);
        ResultSet result = stm.getResultSet();

        while(result.next()) {
            int id = result.getInt("id");
            int pubId = result.getInt("public_id");
            int count = result.getInt("count_people");
            int niche = result.getInt("niche");
            int unix_time = result.getInt("last_post_unix");
            groups.add(new ParsedGroup(id, pubId, count, niche, unix_time));
        }

        result.close();
        stm.close();
        return groups;
    }

    public void setUpdatedParsedGroup(ParsedGroup gr) throws SQLException {
        Statement stm = connection.createStatement();
        String query = String.format("UPDATE parse_from SET " +
                "last_parse_time = NOW()" +
                "WHERE id = %d;", gr.id);
        int updated = stm.executeUpdate(query);
        if (updated < 1) {
            throw new RuntimeException("updated 0 entity");
        }
        stm.close();
    }

    public void setUpdateUnixTime(ParsedGroup gr, int unix) throws SQLException {
        Statement stm = connection.createStatement();
        String query = String.format("UPDATE parse_from SET " +
                "last_post_unix = %d" +
                "WHERE id = %d;", unix, gr.id);
        int updated = stm.executeUpdate(query);
        if (updated < 1) {
            throw new RuntimeException("updated 0 entity");
        }
        stm.close();
    }
}
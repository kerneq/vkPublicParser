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
        ResultSet result = stm.getResultSet();

        result.next();
        while(result.next()) {
            System.out.println("yee");
        }

        result.close();
        stm.close();
        return groups;
    }
}

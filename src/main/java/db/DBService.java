package db;

import db.dao.ParsedGroupDAO;
import db.dao.PostDAO;
import db.dataSets.ParsedGroup;
import db.dataSets.ParsedPost;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by iters on 3/14/17.
 */
public class DBService {
    private Connection connection;
    private ParsedGroupDAO dao;
    private PostDAO postDao;
    public static DBService service;

    private DBService() {
        connection = getMysqlConnection();
        dao = new ParsedGroupDAO(connection);
        postDao = new PostDAO(connection);
        // printConnectInfo();
    }

    public static DBService Instance() {
        if (service == null) {
            service = new DBService();
        }
        return service;
    }

    public void updateParsedTime(ParsedGroup gr) {
        try {
            dao.setUpdatedParsedGroup(gr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ParsedGroup> getParsedGroups() {
        try {
            return dao.getParsedPubList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addNewPost(ParsedPost post, ParsedGroup gr) {
        try {
            postDao.addPost(post, gr.niche, gr.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DBService service = new DBService();
        System.out.println(service.getParsedGroups());
    }

    @SuppressWarnings("UnusedDeclaration")
    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("doPost?").          //db name
                    append("user=root&").          //login
                    append("password=00755cnfc");       //password

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printConnectInfo() {
        try {
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

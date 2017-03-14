package brainy;

import db.DBService;
import db.dataSets.ParsedGroup;
import main.Main;

import java.util.List;

/**
 * Created by iters on 3/14/17.
 */
public class Brain {
    public static void main(String[] args) throws Exception {
        // server started
        Thread startServer = new Thread(() -> {
            try {
                Main.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        startServer.start();

        while (!startServer.getState().toString().equalsIgnoreCase("NEW")) {
            Thread.sleep(500);
        }

        List<ParsedGroup> list = new DBService().getParsedGroups();
        System.out.println(list.size());
        System.out.println("hello");
    }
}
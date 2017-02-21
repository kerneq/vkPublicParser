package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.LoginServlet;
import servlets.MainServlet;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by iters on 2/18/17.
 */
public class Main {
    private static int port;
    private static int client_id;
    private static String secret;

    public static void main(String[] args) throws Exception {
        init();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new MainServlet()), "/");
        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        // context.addServlet(new ServletHolder(new CallBackServlet(vk, client_id, secret)), "/callback");
        // context.addServlet(new ServletHolder(new InfoServlet(vk)), "/info");

        Server server = new Server(port);
        server.setHandler(context);

        server.start();
        java.util.logging.Logger.getGlobal().info("Server started");
        server.join();

        /*
        VFS test = new VFS("/home/iters/media");
        test.addEntity("hello", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTJrzGvFkTylKm_BOaBLIUhpDuqA363gtilvNIZTV2Y85zWcSq6jksF_cQ");
        test.addEntity("http://minionomaniya.ru/wp-content/uploads/2016/01/%D0%BC%D0%B8%D0%BD%D1%8C%D0%BE%D0%BD%D1%8B-%D0%BF%D1%80%D0%B8%D0%BA%D0%BE%D0%BB%D1%8B-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8.jpg");
        */
    }

    private static void init() {
        Properties properties = new Properties();
        try (java.io.InputStream is = Main.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        port = Integer.parseInt(properties.getProperty("server.port"));
        client_id = Integer.parseInt(properties.getProperty("client.id"));
        secret = properties.getProperty("client.secret");
    }
}
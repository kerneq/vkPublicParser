package main;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.Auth.AuthServlet;
import servlets.Auth.InfoServlet;
import servlets.LoginServlet;
import servlets.MainServlet;
import servlets.Auth.VkResponseRedirect;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by iters on 2/18/17.
 */
public class Main {
    private static int port;
    private static int client_id;
    private static String secret;

    private static String token = "0d1f9f2cb46be8b83047d313a58f0c54d6ba7a26bd20469e82a373463f09dd995fc0c20f63185cd28d546";
    private static int user = 36952698;
    private static UserActor actor = new UserActor(user, token);

    public static void main(String[] args) throws Exception {
        init();
        VkApiClient vk = new VkApiClient(new HttpTransportClient());

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new MainServlet()), "/");
        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new AuthServlet(client_id)), "/auth");
        context.addServlet(new ServletHolder(new VkResponseRedirect(vk, client_id, secret)), "/response");
        context.addServlet(new ServletHolder(new InfoServlet(vk)), "/info");

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

    public static void setUserActor(UserActor newActor) {
        actor = newActor;
    }

    public static UserActor getUserActor() {
        return actor;
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
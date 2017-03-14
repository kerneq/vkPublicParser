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

    private static String token = "a0af2260f9b20d57b8e83fdd850d34f3eb92c8ea54dd59e1db1130fbf07a3d9e6518bb2c3a0de259c2c41";
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
        actor = new UserActor(user, token);
    }
}
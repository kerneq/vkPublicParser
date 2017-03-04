package servlets.Auth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by iters on 3/4/17.
 */
public class VkResponseRedirect extends HttpServlet {
    private final VkApiClient vk;
    private static int client_id;
    private static String secret;

    public VkResponseRedirect(VkApiClient vk, int client_id, String secret) {
        this.vk = vk;
        this.client_id = client_id;
        this.secret = secret;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserAuthResponse authResponse = vk.oauth().userAuthorizationCodeFlow(
                    client_id, secret,
                    "http://0.0.0.0:8000/response",
                    req.getParameter("code")).execute();

            resp.sendRedirect("http://0.0.0.0:8000/info?token=" +
                    authResponse.getAccessToken() + "&user=" +
                    authResponse.getUserId());

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
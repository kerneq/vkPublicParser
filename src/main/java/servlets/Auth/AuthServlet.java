package servlets.Auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by iters on 3/4/17.
 */
public class AuthServlet extends HttpServlet {
    private int client_id;

    public AuthServlet(int client_id) {
        this.client_id = client_id;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(getOAuthUrl());
    }

    private String getOAuthUrl() {
        return "https://oauth.vk.com/authorize?client_id=" +
                client_id + "&display=page&redirect_uri=" +
                getRedirectUri() +
                "&scope=photos,groups,manage&response_type=code";
    }

    private String getRedirectUri() {
        return "http://0.0.0.0:8000/response";
    }

}

package servlets.Auth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import main.Main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by iters on 3/4/17.
 */
public class InfoServlet extends HttpServlet {
    private VkApiClient vk;

    public InfoServlet(VkApiClient vk) {
        this.vk = vk;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserActor actor = new UserActor(
                Integer.parseInt(req.getParameter("user")),
                req.getParameter("token"));
        Main.setUserActor(actor);

        List<UserXtrCounters> getUsersResponse = null;
        try {
            getUsersResponse = vk.users().get(actor).userIds(req.getParameter("user")).execute();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        UserXtrCounters user = getUsersResponse.get(0);
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        // resp.getWriter().println(getInfoPage(user));
        resp.sendRedirect("http://0.0.0.0:8000/");
    }

    private String getInfoPage(UserXtrCounters user) {
        return "Hello <a href='https://vk.com/id" + user.getId() + "'>" + user.getFirstName() + "</a>";
    }
}
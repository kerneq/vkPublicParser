package servlets;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.WallpostFull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by iters on 2/19/17.
 */
public class InfoServlet extends HttpServlet {
    private VkApiClient vk;

    public InfoServlet(VkApiClient vk) {
        this.vk = vk;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserActor actor = new UserActor(Integer.parseInt(req.getParameter("user")), req.getParameter("token"));

        try {
            List<WallpostFull> list =  vk.wall().get().ownerId(-35806476).execute().getItems();
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().print(list.get(1).getAttachments().get(0));
            resp.getWriter().print(list.get(1).getPostType().getValue());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
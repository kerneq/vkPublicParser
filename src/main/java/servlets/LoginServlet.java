package servlets;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import handlers.ImgUrlParser;
import handlers.VFS;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by iters on 2/18/17.
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        int public_id = Integer.parseInt(req.getParameter("id"));
        int max = Integer.parseInt(req.getParameter("count"));

        String root = req.getParameter("root");
        VFS vfs = new VFS(root);

        List<WallpostFull> list = null;
        try {
            list = vk.wall().get().ownerId((-1)*public_id).execute().getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.setContentType("text/html;charset=utf-8");

        for (int i = 0; i < list.size() && i < max; i++) {
            boolean isParse = false;

            // trying to get title text
            try {
                String text = list.get(i).getText();
                vfs.addEntityTextOnly(text);
                isParse = true;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            // trying to get attachments
            try {
                List<WallpostAttachment> attach = list.get(i).getAttachments();
                for (int j = 0; j < attach.size(); j++) {
                    String imgUrl = ImgUrlParser.getUrlFromSrc(attach.get(j).toString());
                    vfs.addEntity(imgUrl);
                }
                isParse = true;
            } catch (Exception e) {
                // vfs.rollBack();
                // e.printStackTrace();
            }

            if (isParse) {
                vfs.nextStep();
            } else {
                max++;
            }
        }

        resp.getWriter().println("Done!");
    }
}
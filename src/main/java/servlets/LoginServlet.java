package servlets;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import handlers.IdPublicHandler;
import handlers.ImgUrlParser;
import handlers.VFS;
import main.Main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by iters on 2/18/17.
 * Parser, that's parse only text with images or just text
 */
public class LoginServlet extends HttpServlet {
    public final int maxPhotoInDir = 2;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        int public_id = IdPublicHandler.getID(req.getParameter("id"));
        int max = Integer.parseInt(req.getParameter("count"));
        int minReposts = ("".equalsIgnoreCase(req.getParameter("repost")))? 0 :
                Integer.parseInt(req.getParameter("repost"));
        int minLikes = ("".equalsIgnoreCase(req.getParameter("likes")))? 0 :
                Integer.parseInt(req.getParameter("likes"));

        String root = req.getParameter("root");
        VFS vfs = new VFS(root);

        List<WallpostFull> list = null;
        try {
            //list = vk.wall().get().ownerId((-1)*public_id).execute().getItems();
            list = vk.wall().get().ownerId((-1)*public_id).count(100).offset(0).execute().getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(list.size());
        resp.setContentType("text/html;charset=utf-8");

        for (int i = 0; i < list.size() && i < max; i++) {
            boolean isParse = false;

            // optional quality checker
            if (list.get(i).getLikes().getCount() < minLikes ||
                    list.get(0).getReposts().getCount() < minReposts) {
                max++;
                // vfs.rollBack();
                continue;
            }

            // trying to get title text
            try {
                String text = list.get(i).getText();
                isParse = vfs.addEntityTextOnly(text);
                System.out.println("Date is: " + list.get(i).getDate());
            } catch (NullPointerException e) {
                // LOGGING
            }

            int addedPhoto = 0;
            // trying to get attachments
            try {
                List<WallpostAttachment> attach = list.get(i).getAttachments();
                addedPhoto = 0;
                for (int j = 0; j < attach.size(); j++) {
                    // check attach type == photo
                    if (!"photo".equalsIgnoreCase(attach.get(j).getType().getValue())) {
                        // throw new RuntimeException();
                        vfs.rollBack();
                        continue;
                    }

                    String imgUrl = ImgUrlParser.getUrlFromSrc(attach.get(j).toString());
                    if (!vfs.addEntity(imgUrl)) {
                        System.out.println("откат!");
                        vfs.rollBack();
                    } else {
                        addedPhoto++;
                        isParse = true;
                    }

                    // too many images i can't store
                    if (addedPhoto > maxPhotoInDir) {
                        break;
                    }
                }
            } catch (Exception e) {
                // fs.rollBack();
                // LOGGING
            }

            // gain likes, reposts, comments for statistic
            int likes = 0, reposts = 0, comments = 0;
            try {
                likes = list.get(i).getLikes().getCount();
                reposts = list.get(i).getReposts().getCount();
                comments = list.get(i).getComments().getCount();
            } catch (Exception e) {
                // LOGGING
            } finally {
                vfs.addStatistic(likes, reposts, comments);
            }

            // check if was a content here
            if (isParse) {
                vfs.nextStep();
            } else if (!isParse || addedPhoto == 0) {
                System.out.println("откат");
                max++;
                vfs.rollBack();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        resp.getWriter().println("Done!");
    }
}
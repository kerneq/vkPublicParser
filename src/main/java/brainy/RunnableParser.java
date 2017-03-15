package brainy;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import db.DBService;
import db.dataSets.ParsedGroup;
import handlers.ImgUrlParser;
import handlers.VFS;
import java.util.List;

/**
 * Created by iters on 3/15/17.
 */
public class RunnableParser implements Runnable {
    private int maxPhotoInDir = 2;

    @Override
    public void run() {
        List<ParsedGroup> list = new DBService().getParsedGroups();
        for (ParsedGroup gr : list) {
            parse(gr.pubId, 10);

            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void parse(int public_id, int max) {
        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        String root = "/home/iters/media/";
        VFS vfs = new VFS(root);

        List<WallpostFull> list = null;
        try {
            //list = vk.wall().get().ownerId((-1)*public_id).execute().getItems();
            list = vk.wall().get().ownerId((-1)*public_id).count(100).offset(0).execute().getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < list.size() && i < max; i++) {
            boolean isParse = false;

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
    }
}
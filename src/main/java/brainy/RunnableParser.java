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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by iters on 3/15/17.
 */
public class RunnableParser implements Runnable {
    private int maxPhotoInDir = 3; // in fact 4

    @Override
    public void run() {
        DBService db = DBService.Instance();
        // daemon
        while (true) {
            List<ParsedGroup> list = db.getParsedGroups();
            for (ParsedGroup gr : list) {
                parse(gr.pubId, 10, gr);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                db.updateParsedTime(gr);
            }

            try {
                TimeUnit.MINUTES.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parse(int public_id, int max, ParsedGroup gr) {
        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        String root = "/home/iters/media/";
        VFS vfs = new VFS(root);
        Pattern pattern = Pattern.compile(".*\\[club\\d+\\|.*]");
        Matcher matcher = pattern.matcher("");

        List<WallpostFull> list = null;
        try {
            //list = vk.wall().get().ownerId((-1)*public_id).execute().getItems();
            list = vk.wall().get().ownerId((-1)*public_id).count(100).offset(0).execute().getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isTimeUnixUpdated = false;
        for (int i = 0; i < list.size() && i < max; i++) {
            boolean isParse = false;

            // don't parse pinned posts
            if (list.get(i).getIsPinned() != null) {
                System.out.println("PINNED POST");
                continue;
            }

            // check date from db
            if (list.get(i).getDate() <= gr.last_post_unix) {
                break;
            }

            // update unix time
            if (!isTimeUnixUpdated) {
                int unixTime = list.get(i).getDate();
                DBService.Instance().updateUnixTimeGroup(gr, unixTime);
                isTimeUnixUpdated = true;
            }

            // trying to get title text
            try {
                String text = list.get(i).getText();
                matcher.reset(text);
                if (matcher.find()) {
                    continue;
                }

                // posts can be without text. isParse only check images
                // isParse = vfs.addEntityTextOnly(text);
                vfs.addEntityTextOnly(text);
            } catch (NullPointerException e) {
                // LOGGING
                // we don't need images without text
                continue;
            }

            int addedPhoto = 0;
            // trying to get attachments
            try {
                List<WallpostAttachment> attach = list.get(i).getAttachments();
                addedPhoto = 0;
                // TODO: check attachments size
                for (int j = 0; j < attach.size(); j++) {
                    // check attach type == photo
                    if (!"photo".equalsIgnoreCase(attach.get(j).getType().getValue())) {
                        // throw new RuntimeException();
                        vfs.rollBack();
                        continue;
                    }

                    String imgUrl = ImgUrlParser.getUrlFromSrc(attach.get(j).toString());
                    if (!vfs.addEntity(imgUrl)) {
                        // System.out.println("откат!");
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
                DBService.Instance().addNewPost(vfs.getPost(), gr);
                vfs.nextStep();
            } else if (addedPhoto == 0) {
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
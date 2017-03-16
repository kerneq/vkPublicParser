package handlers;

import com.vk.api.sdk.objects.photos.Photo;
import db.dataSets.ParsedPost;
import db.dataSets.PhotoPost;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by iters on 2/21/17.
 */
public class VFS {
    private final String root;
    private int counter;
    private int imgName;
    private File destDir;

    // post information
    private String headText;
    private List<PhotoPost> photos;
    private ParsedPost post;

    public VFS(String root) {
        this.root = (root.endsWith(File.separator))?root : root + File.separator;
        counter = 1;
        imgName = 1;
        newSession();
        photos = new ArrayList<>();
        post = new ParsedPost();
    }

    private void newSession() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy hh:mm:ss");
        destDir = new File(root + formatter.format(today));
        destDir.mkdir();
    }

    public boolean addEntityTextOnly(String text) {
        File locDir = null;

        if (text.trim().length() == 0) {
            return false;
        }

        try {
            locDir = new File(destDir.getCanonicalFile() + File.separator +  counter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!locDir.exists()) {
            createDir(locDir);
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(locDir.getCanonicalPath()
                + File.separator + "title.txt"))) {
            pw.print(text);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        headText = text;
        return true;
    }

    public boolean addEntity(String url) {
        boolean success = false;
        File locDir = null;
        try {
            locDir = new File(destDir.getCanonicalFile() + File.separator +  counter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!locDir.exists()) {
            createDir(locDir);
        }

        try(java.io.InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(locDir.getCanonicalPath()
                    + File.separator + imgName++ + ".jpg"));
            success = true;
        } catch (IOException e) {
            // LOGGING
        }

        if (success) {
            try {
                String imgPath = locDir.getCanonicalPath() +
                        File.separator + (imgName - 1) + ".jpg";
                List<Photo> photo = MultipartPostReq.getUploadedImages(new File(imgPath));

                String photoUrl = ImgUrlParser.getUrlFromSrc(photo.get(0).toString());
                String photoId = ImgLoaderParser.getIdFromImg(photo.get(0).toString());
                String photoAlbumId = ImgLoaderParser.getAlbumIdFromImg(photo.get(0).toString());
                String photoOwnerId = ImgLoaderParser.getOwnerIdFromImg(photo.get(0).toString());

                File info = new File(locDir.getCanonicalPath() +
                        File.separator + (imgName - 1) + ".txt");
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(info))) {
                    bw.write("url:" + photoUrl + "\n");
                    bw.write("id:" + photoId + "\n");
                    bw.write("albumId:" + photoAlbumId + "\n");
                    bw.write("ownerId:" + photoOwnerId);
                }

                photos.add(new PhotoPost(
                        Integer.parseInt(photoId),
                        Integer.parseInt(photoAlbumId),
                        Integer.parseInt(photoOwnerId),
                        url
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    public void addStatistic(int likes, int reposts, int comments) {
        File locDir = null;
        try {
            locDir = new File(destDir.getCanonicalFile() + File.separator +  counter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(PrintWriter pw = new PrintWriter(locDir.getCanonicalPath()
                + File.separator + "statistic.txt")) {
            pw.write("likes:" + likes + "\n");
            pw.write("reposts:" + reposts + "\n");
            pw.write("comments:" + comments);
            post.setUp(likes, reposts, comments);
        } catch (FileNotFoundException e) {
            // LOGGING
            // e.printStackTrace();
        } catch (IOException e) {
            // LOGGING
            // e.printStackTrace();
        }
    }

    public void rollBack() {
        try {
            FileUtils.deleteDirectory(
                    new File(destDir.getCanonicalFile() +
                            File.separator +
                            counter +
                            File.separator)
            );
            headText = null;
            photos.clear();
            post = new ParsedPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDir(File dir) {
        dir.mkdir();
    }

    public void nextStep() {
        counter++;
        imgName = 1;
        headText = null;
        photos.clear();
        post = new ParsedPost();
    }

    public static void main(String[] args) throws IOException {
        FileUtils.deleteDirectory(new File("/home/iters/media/1/"));
    }
}
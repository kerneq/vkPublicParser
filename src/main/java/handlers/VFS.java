package handlers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by iters on 2/21/17.
 */
public class VFS {
    private final String root;
    private int counter;
    private int imgName;
    private File destDir;

    public VFS(String root) {
        this.root = (root.endsWith(File.separator))?root : root + File.separator;
        counter = 1;
        imgName = 1;
        newSession();
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
                    + File.separator + imgName++));
            success = true;
        } catch (IOException e) {
            // LOGGING
        }

        return success;
    }

    public void rollBack() {
        try {
            FileUtils.deleteDirectory(new File(destDir.getCanonicalFile() + File.separator +  counter));
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
    }
}
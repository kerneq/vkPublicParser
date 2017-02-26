package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by iters on 2/26/17.
 */
public class IdPublicHandler {
    private static int getID(String url) {
        String str = getStringWithId(url);
        String find = "/wall-";
        str = str.substring(str.indexOf(find) + find.length(), str.length());
        str = str.substring(0, str.indexOf("_"));
        return Integer.parseInt(str);
    }

    public static String getStringWithId(String url) {
        URL pub = null;

        try {
            pub = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try(BufferedReader in = new BufferedReader(
                new InputStreamReader(pub.openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                if (inputLine.contains("/wall-"))
                    return inputLine;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(getID("https://vk.com/vcru"));
    }
}
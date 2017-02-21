package handlers;

/**
 * Created by iters on 2/22/17.
 */
public class ImgUrlParser {
    public static String getUrlFromSrc(String text) {
        String find = "photo604='";
        int start = text.indexOf(find) + find.length();
        int end = text.indexOf('\'', start + 1);
        return text.substring(start, end);
    }
}

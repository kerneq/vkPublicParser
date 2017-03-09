package handlers;

/**
 * Created by iters on 3/10/17.
 */
public class ImgLoaderParser {
    //TODO: link to one method

    public static String getIdFromImg(String text) {
        String find = "id=";
        int start = text.indexOf(find) + find.length();
        int end = text.indexOf(',', start + 1);
        return text.substring(start, end);
    }

    public static String getAlbumIdFromImg(String text) {
        String find = "albumId=";
        int start = text.indexOf(find) + find.length();
        int end = text.indexOf(',', start + 1);
        return text.substring(start, end);
    }

    public static String getOwnerIdFromImg(String text) {
        String find = "ownerId=";
        int start = text.indexOf(find) + find.length();
        int end = text.indexOf(',', start + 1);
        return text.substring(start, end);
    }

    public static void main(String[] args) {
        String s = "Photo{id=456239027, albumId=241636764, ownerId=-140860188, userId=36952698, sizes=null, photo75='https://pp.userapi.com/c637631";
        System.out.println(getIdFromImg(s));
        System.out.println(getAlbumIdFromImg(s));
        System.out.println(getOwnerIdFromImg(s));
    }
}

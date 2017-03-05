package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import main.Main;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iters on 3/4/17.
 */
public class MultipartPostReq {
    public static JsonObject sendImages(String url, List<File> imgs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity();

        int counter = 1;
        for (File file: imgs) {
            FileBody uploadFilePart = new FileBody(file);
            reqEntity.addPart("file" + counter++, uploadFilePart);
        }
        httpPost.setEntity(reqEntity);

        try {
            StringBuilder answer = new StringBuilder();
            HttpResponse response = httpClient.execute(httpPost);
            BufferedReader bf =  new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String str;
            while ((str = bf.readLine()) != null) {
                  answer.append(str);
            }

            Gson gson = new GsonBuilder().create();
            JsonElement element = gson.fromJson(answer.toString(), JsonElement.class);
            JsonObject object = element.getAsJsonObject();

            bf.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws ClientException, ApiException {
        List<File> imgs = new ArrayList<>();
        File photo = new File("/home/iters/media/123/1/1.jpg");
        imgs.add(photo);

        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        UserActor actor = Main.getUserActor();
        PhotoUpload photoVk =  vk.photos().
                getUploadServer(actor).
                albumId(241636764).
                groupId(140860188).
                execute();

        System.out.println(photoVk.toString());
        System.out.println("-------------------------------");

        JsonObject req =  sendImages(photoVk.getUploadUrl(), imgs);

        System.out.println(req.toString());
        System.out.println("-------------------------------");

        String server = req.get("server").toString();
        System.out.println("server: " + server);

        String photos_list = req.get("photos_list").toString().replaceAll("\\\\", "");
        photos_list = photos_list.substring(1, photos_list.length() - 1);
        System.out.println("photos_list: " + photos_list);

        String albumId = req.get("aid").toString();
        System.out.println("album: " + albumId);

        String hash = req.get("hash").toString().replaceAll("\"", "");
        System.out.println("hash: " + hash);

        System.out.println(actor.toString());

        List<Photo> ph =  vk.photos().save(actor).
                server(Integer.parseInt(server)).
                photosList(photos_list).
                albumId(Integer.parseInt(albumId)).
                hash(hash).groupId(140860188).execute();
        System.out.println("size: " + ph.size());
        System.out.println(ph.get(0).toString());
    }
}
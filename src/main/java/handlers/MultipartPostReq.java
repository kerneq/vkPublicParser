package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

            System.out.println(object.get("server"));
            bf.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        List<File> imgs = new ArrayList<>();
        File photo = new File("/home/iters/media/123/1/1.jpeg");
        imgs.add(photo);
        sendImages("https://pu.vk.com/c637631/upload.php?act=do_add&mid=36952698&aid=241636764&gid=140860188&hash=f5a604cea201c8eff7786f0a2e5efb3b&rhash=b25c0542f08a6212ad9ab77e9af31b0f&swfupload=1&api=1", imgs);
    }
}
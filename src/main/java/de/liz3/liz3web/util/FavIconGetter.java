package de.liz3.liz3web.util;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.sf.image4j.codec.ico.ICODecoder;
import org.binaryone.jutils.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yannh on 18.04.2017.
 */
public class FavIconGetter {

    private String path;
    private File folder;
    private HashMap<Long, File> saved;

    public FavIconGetter(String path) {

        saved = new HashMap<>();
        this.path = path;
        folder = new File(path, "icons");

        prepare();
    }

    private void save() {

        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        for (long id : saved.keySet()) {
            JSONObject current = new JSONObject();
            current.put("id", id);
            current.put("path", saved.get(id).getAbsolutePath());
            arr.put(current);
        }
        obj.put("pathes", arr);

        FileUtils.writeFile(obj.toString(), new File(folder, "info.liz3web"));
    }

    public ImageView getPicture(String url, long id) {
        if (!saved.containsKey(id)) {
            InputStream in = HttpMethods.getFavIcon(url);
            File ico = new File(folder, id + ".ico");
            File png = new File(folder, id + ".png");
            try {
                IOUtils.writeByteArrayToFile(ico, IOUtils.convertStreamToByteArray(in));
            } catch (IOException e) {

                return null;
            }
            try {
                List<BufferedImage> images = ICODecoder.read(ico);
                ImageIO.write(images.get(0), "png", png);
            } catch (IOException e) {
                if(ico.exists()) ico.delete();
                return null;
            }
            new Thread(() -> {
                if(ico.exists()) ico.delete();
            }).start();
            saved.put(id, png);
            save();
        }
        File f = saved.get(id);
        Image img = null;
        try {
            img = new Image(f.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ImageView view = new ImageView(img);
        view.setFitWidth(16);
        view.setFitHeight(16);
        return view;
    }

    private void prepare() {


        if (!folder.exists()) {
            folder.mkdir();
            try {
                new File(folder, "info.liz3web").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

       try {
           JSONObject obj = new JSONObject(FileUtils.readFile(new File(folder, "info.liz3web")));

           JSONArray arr = obj.getJSONArray("pathes");

           for (int i = 0; i < arr.length(); i++) {
               JSONObject current = arr.getJSONObject(i);

               String path = current.getString("path");
               long id = current.getLong("id");
               File file = new File(path);

               if (file.exists()) saved.put(id, file);


           }
       }catch (Exception e) {
           saved.clear();
       }
        }
    }

}

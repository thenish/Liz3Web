package de.liz3.liz3web.browserinc;

import de.liz3.liz3web.util.IOUtils;
import org.binaryone.jutils.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by yannh on 17.04.2017.
 */
public class FavoriteManager {


    private HashMap<String, BookMarkEntry> entries;
    private String rootPath;

    public FavoriteManager(String rootPath) {
        entries = new HashMap<>();
        this.rootPath = rootPath;
        loadIn();
    }

    public void add(String url, String name) {

        if(entries.containsKey(url)) return;
        entries.put(url, new BookMarkEntry(entries.size() +1 , name, url, url, null));

        save();
    }
    public void save() {

        JSONObject obj = new JSONObject();
        JSONObject roots = new JSONObject();
        JSONObject bookmarks = new JSONObject();
        JSONArray arr = new JSONArray();
        for(BookMarkEntry e : entries.values()) {
            JSONObject current = new JSONObject();
            current.put("id", e.getId());
            current.put("name", e.getName());
            current.put("url", e.getUrl());
            arr.put(current);
        }
        bookmarks.put("children", arr);
        roots.put("bookmark_bar", bookmarks);
        obj.put("roots", roots);
        obj.put("checksum", roots.toString().hashCode());

        FileUtils.writeFile(obj.toString(), new File(rootPath, "bookmarks.liz3web"));
    }
    public void loadIn() {

        entries.clear();
        File f = new File(rootPath, "bookmarks.liz3web");

        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            JSONObject obj = new JSONObject(IOUtils.convertStreamToString(new FileInputStream(f)));

            JSONObject bookmarks = obj.getJSONObject("roots").getJSONObject("bookmark_bar");
            JSONArray arr = bookmarks.getJSONArray("children");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject current = arr.getJSONObject(i);
                String url = current.getString("url");
                String name = current.getString("name");
                entries.put(url, new BookMarkEntry(i, name, url, url, null));
                System.out.println("Loaded new Bookmark");

            }

        } catch (Exception e) {
            entries.clear();
        }
    }

    public HashMap<String, BookMarkEntry> getEntries() {
        return entries;
    }
}

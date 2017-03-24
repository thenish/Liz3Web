package de.liz3.liz3web.browser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

import java.util.*;

/**
 * Created by yannh on 25.02.2017.
 */
public class History {

    private Map<String, HistoryEntry> entrys;
    private boolean loaded;
    private long index;

    public History() {

        entrys = new HashMap<>();

    }

    public void loadIn() {

    try {
        File f = new File("history.txt");

        if (!f.exists()) {
            loaded = true;
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            index = 0;
        } else {

            try {
                Scanner sc = new Scanner(f);

                String all = "";

                while (sc.hasNextLine()) {

                    all += sc.nextLine();
                }
                sc.close();

                JSONArray arr = new JSONArray(all);


                for (int i = 0; i != arr.length(); i++) {

                    JSONObject obj = arr.getJSONObject(i);


                    HistoryEntry entry = new HistoryEntry(obj.getString("link"), new Date(), obj.getLong("id"));

                    entrys.put(obj.getString("link"), entry);
                }

                loaded = true;
                index = arr.length();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }catch (Exception e) {

    }

    }

    public void addEntry(String url) {


        boolean x = false;

        if (entrys.containsKey(url)) {
            long c = entrys.get(url).getId();
            entrys.remove(url);
            x = true;

            for (HistoryEntry entry : entrys.values()) {
                if(entry.getId() > c) {
                    entry.setId(entry.getId() - 1);
                }
            }

        }

        if (!x)
            index++;
        entrys.put(url, new HistoryEntry(url, new Date(), index));

        save();
    }

    public void save() {

        JSONArray arr = new JSONArray();

        for (HistoryEntry entry : entrys.values()) {

            JSONObject obj = new JSONObject();

            obj.put("link", entry.getUrl());
            obj.put("id", entry.getId());
            obj.put("unix", entry.getUnixTime());

            arr.put(obj);
        }
        new Thread(() -> {
            try {
                PrintWriter w = new PrintWriter(new FileWriter(new File("history.txt")));

                w.println(arr.toString());

                w.flush();
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Map<String, HistoryEntry> getEntrys() {
        return entrys;
    }

    public long getIndex() {
        return index;
    }

    public boolean isLoaded() {
        return loaded;
    }
}

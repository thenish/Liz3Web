package de.liz3.liz3web.browser;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.ProtocolService;
import com.teamdev.jxbrowser.chromium.URLResponse;
import de.liz3.liz3web.gui.GuiManager;
import de.liz3.liz3web.util.IOUtils;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yannh on 25.02.2017.
 */
public class LocalHandler {

    public static void handle(BrowserTab tab, String url) {

        new Thread(() -> {

            Browser b = tab.getBrowser();


            BrowserContext browserContext = b.getContext();
            ProtocolService protocolService = browserContext.getProtocolService();


            protocolService.setProtocolHandler("locals", request -> {

                URLResponse response = new URLResponse();
                String html = "";
                System.out.println(request.getURL());
                if(request.getURL().startsWith("http://data")) {


                } else {

                    String path = request.getURL().split("locals://")[1];



                    try {
                        html = IOUtils.convertStreamToString(LocalHandler.class.getResourceAsStream("/" + path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (path.endsWith(".js")) {
                        response.getHeaders().setHeader("Content-Type", "application/javascript");
                    }
                    if (path.endsWith(".css")) {
                        response.getHeaders().setHeader("Content-Type", "text/css");
                    }


                }

                response.setData(html.getBytes());

                return response;
            });

            Platform.runLater(() -> {


                if (tab.getTab() == GuiManager.currentActive) {
                    GuiManager.browserController.urlField.setText(url);

                }
                if (url.equalsIgnoreCase("locals://history")) {

                    tab.getTab().setText("History");
                    try {
                        b.loadHTML(IOUtils.convertStreamToString(LocalHandler.class.getResourceAsStream("/locals/history.html")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printHistory(b);

                }


            });


        }).start();

    }

    private static void printHistory(Browser b) {

        long entries = GuiManager.history.getIndex();

        if(entries <= 20) {

            JSONObject obj = new JSONObject();

            obj.put("start", 0);
            obj.put("end", entries);
            obj.put("more", false);
            JSONArray arr = new JSONArray();

            for(HistoryEntry entry : GuiManager.history.getEntrys().values()) {

                JSONObject o = new JSONObject();
                o.put("id", entry.getId());
                o.put("url", entry.getUrl());
                o.put("unix", entry.getUnixTime());
                o.put("nice", entry.getDate());
                arr.put(o);

            }

            obj.put("data", arr);


            System.out.println(obj.toString());
            b.executeJavaScript("setTimeout(function () {\n" +
                    "        parse('" + obj.toString() + "');\n" +
                    "    }, 1500);");
        }



    }

}

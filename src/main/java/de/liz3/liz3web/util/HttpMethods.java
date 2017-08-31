package de.liz3.liz3web.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yannh on 30.11.2016.
 */
public class HttpMethods {

    public static String getSource(String url) throws Exception {

        if (!url.toLowerCase().startsWith("https://")) {
            return getAsHttp(url);
        } else {
            return getAsHttps(url);
        }

    }

    public static InputStream getFavIcon(String link) {
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = new BufferedInputStream(url.openStream());

            return in;
        } catch (IOException e) {
           return null;
        }
    }

    private static String getAsHttp(String url) throws IOException {
        URL website = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) website.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();
        return response.toString();
    }

    private static String getAsHttps(String url) throws IOException {
        URL website = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) website.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();
        return response.toString();
    }
}

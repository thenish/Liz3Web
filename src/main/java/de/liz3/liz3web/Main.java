package de.liz3.liz3web;

import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by yannh on 15.04.2017.
 */
public class Main {

    public static void main(String[] args) {

        try {
            Socket s = new Socket("127.0.0.1", 15567);

            if(args[0] != null) {
                String line = "-link " + args[0] + "\n";
                s.getOutputStream().write(line.getBytes());
                s.getOutputStream().flush();
                return;
            }
           return;
        } catch (IOException ignored) {}

        Liz3Web.run(args);
    }
}

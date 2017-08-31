package de.liz3.liz3web.util;

import com.teamdev.jxbrowser.chromium.BrowserType;
import de.liz3.liz3web.Liz3Web;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yannh on 17.04.2017.
 */
public class ClientSocket {

    public ClientSocket() {

    }
    public void startSocket() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket s = new ServerSocket(15567);

                    while (true) {

                        Socket c  = s.accept();

                        new Thread(() -> {

                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));

                                String l = "";

                                while ((l = reader.readLine()) != null) {

                                    if(l.startsWith("-link")) {
                                        Liz3Web.liz3Web.getWindows().get(0).newTab(l.split(" ")[1], BrowserType.HEAVYWEIGHT);

                                        c.close();
                                    }

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }).start();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}

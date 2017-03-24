package de.liz3.liz3web.util;

import de.liz3.liz3web.browser.BrowserTab;
import de.liz3.liz3web.browser.HistoryEntry;
import de.liz3.liz3web.gui.GuiManager;
import javafx.application.Platform;

import java.util.Scanner;

/**
 * Created by yannh on 25.02.2017.
 */
public class CommandLineWorker {

    private Thread thread;

    public CommandLineWorker() {

        thread = new Thread(() -> {


            Scanner sc = new Scanner(System.in);


            while (sc.hasNextLine()) {

                String line = sc.nextLine();
                if (line != null) {
                    proceed(line);
                }
            }

        });
        thread.setName("Commandline Thread");
        thread.start();

    }

    public void proceed(String c) {

        new Thread(() -> {

            String[] args = c.split(" ");
            String command = args[0];
            if (command.equalsIgnoreCase("reset")) {

                if (args.length > 1) {

                    if(args[1].equalsIgnoreCase("history")) {

                        GuiManager.history.getEntrys().clear();
                    }
                    if (args[1].equalsIgnoreCase("tabs")) {
                        for (BrowserTab tab : GuiManager.openTabs) {

                            if (tab.getTab() != GuiManager.browserController.newTab) {
                                Platform.runLater(() -> GuiManager.browserController.tabPane.getTabs().remove(tab.getTab()));
                            }
                        }
                        GuiManager.currentActive = null;
                        GuiManager.openTabs.clear();

                        return;
                    }
                    if (args[1].equalsIgnoreCase("style")) {

                        GuiManager.rootScene.getStylesheets().clear();

                        GuiManager.rootScene.getStylesheets().add("style/AppStyle.css");
                    }
                }
            }
            if(command.equalsIgnoreCase("print")) {

                if(args[1].equalsIgnoreCase("history")) {

                    for(HistoryEntry e : GuiManager.history.getEntrys().values()) {

                        System.out.println("Entry: ");
                        System.out.println("\tLink: " + e.getUrl());
                        System.out.println("\tID: " + e.getId());
                        System.out.println("\tUnix: " + e.getUnixTime());
                        System.out.println("\tDate: " + e.getDate());
                    }
                }
            }

        }).start();

    }
}

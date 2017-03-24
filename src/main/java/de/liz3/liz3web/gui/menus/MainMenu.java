package de.liz3.liz3web.gui.menus;

import de.liz3.liz3web.Main;
import de.liz3.liz3web.browser.BrowserTab;
import de.liz3.liz3web.browser.TabManager;
import de.liz3.liz3web.gui.GuiManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

/**
 * Created by yannh on 30.11.2016.
 */
public class MainMenu {

    public static void createMenu(MenuButton m) {

        MenuItem closePoint = new MenuItem("Close");
        closePoint.setOnAction(event -> Platform.exit());

        MenuItem settingsPoint = new MenuItem("Settings");

        MenuItem historyPoint = new MenuItem("History");

        historyPoint.setOnAction(event -> GuiManager.manager.insertNewTab("locals://history"));

        MenuItem pageSourcePoint = new MenuItem("Debugger");
        pageSourcePoint.setOnAction(event -> GuiManager.manager.getActive().openDebugger());
        MenuItem utilitysPoint = new MenuItem("Utility's");

        MenuItem downloadsPoint = new MenuItem("Downloads");

        MenuItem directFullScreenPoint = new MenuItem("Enter Fullscreen");

        directFullScreenPoint.setOnAction(event -> {
            if(GuiManager.currentActive == null)
                return;
            new Thread(() -> {

                for(BrowserTab tab : GuiManager.openTabs) {
                    if(tab.getTab() == GuiManager.currentActive) {
                        Platform.runLater(() -> tab.fullscreen());

                    }
                }

            }).start();
        });



        m.getItems().addAll(settingsPoint, historyPoint, pageSourcePoint, utilitysPoint, downloadsPoint, directFullScreenPoint, closePoint);
    }
}

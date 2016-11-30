package de.liz3.liz3web.gui.menus;

import javafx.application.Platform;
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

        MenuItem pageSourcePoint = new MenuItem("View Page Source");

        MenuItem utilitysPoint = new MenuItem("Utility's");

        MenuItem downloadsPoint = new MenuItem("Downloads");

        MenuItem directFullScreenPoint = new MenuItem("Enter Fullscreen");




        m.getItems().addAll(settingsPoint, historyPoint, pageSourcePoint, utilitysPoint, downloadsPoint, directFullScreenPoint, closePoint);
    }
}

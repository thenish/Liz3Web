package de.liz3.liz3web.gui;

import com.teamdev.jxbrowser.chromium.BrowserType;
import de.liz3.liz3web.Liz3Web;
import de.liz3.liz3web.core.BrowserTab;
import de.liz3.liz3web.core.BrowserWindow;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Created by yannh on 17.04.2017.
 */
public class MainMenu {

    public static ContextMenu getMenu(BrowserTab tab) {

        ContextMenu contextMenu = new ContextMenu();

        Menu m = new Menu("New Tab");



        MenuItem hW = new MenuItem("Chromium Standard");
        hW.setOnAction(m.getOnAction());

        MenuItem lw = new MenuItem("Lightweight Chromium");
        lw.setOnAction(event -> tab.getWindow().newTab(null, BrowserType.LIGHTWEIGHT));
        MenuItem wb = new MenuItem("Old Webkit(JavaFX WebView)");
        wb.setOnAction(event -> tab.getWindow().newFXTab(null));
        m.getItems().addAll(hW,lw,wb);
        contextMenu.getItems().add(m);
        MenuItem newWindow = new MenuItem("New Window");
        newWindow.setOnAction(event -> {
            BrowserWindow win = new BrowserWindow();
            win.newTab(null, BrowserType.HEAVYWEIGHT);
            Liz3Web.liz3Web.getWindows().add(win);
        });
        contextMenu.getItems().add(newWindow);

        MenuItem source = new MenuItem("Debugger");
        source.setOnAction(event -> tab.source());
        contextMenu.getItems().add(source);

        return contextMenu;
    }
}

package de.liz3.liz3web.gui;

import com.teamdev.jxbrowser.chromium.*;
import de.liz3.liz3web.Liz3Web;
import de.liz3.liz3web.core.BrowserTab;
import de.liz3.liz3web.core.BrowserWindow;
import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.*;

/**
 * Created by yannh on 16.04.2017.
 */
public class Liz3WebContextMenu implements ContextMenuHandler{

    private BrowserTab tab;
    private ContextMenu menu;
    private boolean isVisible;

    public Liz3WebContextMenu(BrowserTab tab) {
        this.tab = tab;
    }
    public void setHandler() {
        tab.getBrowser().setContextMenuHandler(this);

    }


    @Override
    public void showContextMenu(ContextMenuParams contextMenuParams) {

       Platform.runLater(() -> {

           if(isVisible) {
               menu.hide();
               isVisible = false;
               return;
           }
           System.out.println(tab.getBrowserView().getChildren().get(0).toString());

           Point location = contextMenuParams.getLocation();
           Point2D screenLocation = tab.getBrowserView().localToScreen(location.x, location.y);
           menu = new ContextMenu();


           MenuItem reload = new MenuItem("Reload");
           reload.setOnAction(event -> tab.getBrowser().reload());
           menu.getItems().add(reload);
           MenuItem selectAll = new MenuItem("Select All");
           selectAll.setOnAction(event -> tab.getBrowser().executeCommand(EditorCommand.SELECT_ALL));
           menu.getItems().add(selectAll);
           if(!contextMenuParams.getSelectionText().isEmpty()) {
               MenuItem copy = new MenuItem("Copy");
               copy.setOnAction(event -> tab.getBrowser().executeCommand(EditorCommand.COPY));
               menu.getItems().add(copy);


           }
           if(!contextMenuParams.getLinkText().isEmpty()) {

               MenuItem newTab = new MenuItem("Open in new Tab");
               newTab.setOnAction(event -> {
                   new Thread(() -> {
                       try {
                           Thread.sleep(50);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                       tab.getWindow().newTab(contextMenuParams.getLinkURL(), BrowserType.HEAVYWEIGHT);
                       System.gc();
                   }).start();
               });
               MenuItem newWindow = new MenuItem("Open in new Window");
               newWindow.setOnAction(event -> {
                   BrowserWindow win = new BrowserWindow();
                   win.newTab(contextMenuParams.getLinkURL(), BrowserType.HEAVYWEIGHT);

                   Liz3Web.liz3Web.getWindows().add(win);
               });
               menu.getItems().addAll(newTab, newWindow);
           }



           menu.show(tab.getBrowserView(), screenLocation.getX(), screenLocation.getY());

           isVisible = true;
       });
    }
}

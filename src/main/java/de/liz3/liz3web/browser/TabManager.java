package de.liz3.liz3web.browser;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.gui.GuiManager;
import de.liz3.liz3web.gui.controller.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;

/**
 * Created by yannh on 22.02.2017.
 */
public class TabManager {

    private MainController controller;
    public TabManager() {

    }

    public void newTab() {

    new Thread(() -> Platform.runLater(() -> {


        controller.tabPane.getTabs().remove(controller.newTab);
        Tab tab = new Tab("new tab");
        Browser browser = new Browser();
        BrowserView newView = new BrowserView(browser);
        tab.setContent(newView);
        controller.tabPane.getTabs().add(tab);
        GuiManager.openTabs.add(new BrowserTab(browser, newView, tab, controller.urlField));
        controller.tabPane.getSelectionModel().select(tab);
        controller.tabPane.getTabs().add(controller.newTab);
        browser.loadURL("");
        controller.urlField.setText("about:blank");
        GuiManager.currentActive = tab;

    })).start();

    }
    public BrowserTab getActive() {

        for(BrowserTab tab : GuiManager.openTabs) {

            if(tab.getTab() == GuiManager.currentActive) {
                return tab;
            }
        }
        return null;
    }
    public void insertNewTab(String url) {


        new Thread(() -> {



            Browser newBrowser = new Browser();
            BrowserView newView = new BrowserView(newBrowser);

            Tab tab = new Tab(url);
            tab.setContent(newView);

            BrowserTab t = new BrowserTab(newBrowser, newView, tab, controller.urlField);
            GuiManager.openTabs.add(t);



            Task<Runnable> create = new Task<Runnable>() {
                @Override
                protected Runnable call() throws Exception {

                    Platform.runLater(() -> {


                        controller.tabPane.getTabs().remove(controller.newTab);
                        controller.tabPane.getTabs().add(tab);
                        controller.tabPane.getSelectionModel().select(tab);
                        controller.tabPane.getTabs().add(controller.newTab);

                        GuiManager.currentActive = tab;

                       if(url.toLowerCase().startsWith("locals://")) {

                           LocalHandler.handle(t, url);
                           GuiManager.history.addEntry(url);

                       } else {

                           newBrowser.loadURL(url);
                           GuiManager.history.addEntry(url);
                       }

                    });

                    return null;
                }
            };
            new Thread(create).start();
        }).start();

    }
    public MainController getController() {
        return controller;
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}

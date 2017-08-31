package de.liz3.liz3web.core;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.Liz3Web;
import de.liz3.liz3web.gui.controller.BrowserTabController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by yannh on 15.04.2017.
 */
public class BrowserWindow {

    private HashMap<Tab, BrowserTab> tabs;
    private HashMap<Tab, FXTab> fxTabs;
    private BorderPane pane;
    private Stage stage;
    private TabPane tabPane;
    private Tab newTab;
    private boolean visible;
    private boolean mousePressed;


    public BrowserWindow() {
        prepare();
    }

    public BrowserWindow(Stage stage) {
        this.stage = stage;
        prepare();
    }

    public BrowserWindow(BrowserTab tab) {
        prepare();
        addFirst(tab);
    }
    public BrowserWindow(FXTab fxTab) {
        prepare();
        addFirst(fxTab);
    }
    public BrowserWindow(BrowserTab tab, Stage stage) {
        this.stage = stage;
        prepare();
        addFirst(tab);
    }

    public void show() {
        if (visible) return;
        visible = true;
        stage.show();
    }

    public void hide() {
        if (!visible) return;
        visible = false;
        stage.hide();
    }
    private void addFirst(FXTab tab) {
        tabPane.getTabs().add(tab.getTab());
        fxTabs.put(tab.getTab(), tab);
        tabPane.getTabs().remove(newTab);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(tab.getTab());
        tab.setWindow(this);
    }
    private void addFirst(BrowserTab tab) {

        tabs.put(tab.getTab(), tab);
        tabPane.getTabs().add(tab.getTab());

        tabPane.getTabs().remove(newTab);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(tab.getTab());

    }

    private void prepare() {
        Liz3Web.liz3Web.debug("Creating Browser window..", 0);

        Liz3Web.liz3Web.debug("Creating gui elements...", 3);
        tabs = new HashMap<>();
        fxTabs = new HashMap<>();
        pane = new BorderPane();
        Liz3Web.liz3Web.debug("Checking for preset Stage...", 3);
        if (stage == null) stage = new Stage();
        Liz3Web.liz3Web.debug("Stage is: " + stage.toString(), 3);
        Liz3Web.liz3Web.debug("Creating tab pane...", 3);
        tabPane = new TabPane();
        Liz3Web.liz3Web.debug("Setting Window preferences...", 3);
        pane.setCenter(tabPane);
        Scene s = new Scene(pane, 800, 400);


        s.getStylesheets().add("style/BrowserWindow.css");
        stage.setScene(s);
        stage.setTitle("Liz3Web");
        stage.centerOnScreen();
        stage.show();
        visible = true;
        Liz3Web.liz3Web.debug("Creating + Tab...", 3);
        newTab = new Tab("+");
        newTab.setClosable(false);
        tabPane.getTabs().add(newTab);
        Liz3Web.liz3Web.debug("Creating toListener for the Browser window: " + this.toString(), 3);

        tabPane.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.SECONDARY) {

                ContextMenu m = new ContextMenu();

                MenuItem item = new MenuItem("Open in new Window");

                item.setOnAction(event1 -> {

                    if (tabPane.getSelectionModel().getSelectedItem() == null) return;

                    if(tabs.containsKey(tabPane.getSelectionModel().getSelectedItem())) {
                        BrowserTab tab = tabs.get(tabPane.getSelectionModel().getSelectedItem());
                        tabPane.getTabs().remove(tab.getTab());
                        BrowserWindow win = new BrowserWindow(tab);
                        Liz3Web.liz3Web.getWindows().add(win);
                        tab.setWindow(win);
                        tabs.remove(tab.getTab());
                    } else if(fxTabs.containsKey(tabPane.getSelectionModel().getSelectedItem())) {
                        FXTab t = fxTabs.get(tabPane.getSelectionModel().getSelectedItem());
                        BrowserWindow win = new BrowserWindow(t);

                        fxTabs.remove(t);
                        Platform.runLater(() -> {
                            if(tabPane.getTabs().contains(t.getTab())) {
                                tabPane.getTabs().remove(t.getTab());
                            }
                        });
                    }
                });

                m.getItems().add(item);
                m.show(tabPane, event.getScreenX(), event.getScreenY());
            }
        });
        tabPane.setOnMouseReleased(event -> mousePressed = false);
        tabPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                mousePressed = true;
                if (tabPane.getSelectionModel().getSelectedItem() == newTab) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(300);

                                if (mousePressed) {
                                    ContextMenu c = new ContextMenu();

                                    MenuItem leightweight = new MenuItem("Lightweight Chromium");
                                    MenuItem heavyWeight = new MenuItem("Standard Chromium");
                                    MenuItem jx = new MenuItem("Old WebKit(JavaFX WebView)");
                                    leightweight.setOnAction(event12 -> newTab(null, BrowserType.LIGHTWEIGHT));
                                    heavyWeight.setOnAction(event13 -> newTab(null, BrowserType.HEAVYWEIGHT));
                                    jx.setOnAction(event14 -> newFXTab(null));
                                    c.getItems().addAll(leightweight, heavyWeight, jx);

                                    Platform.runLater(() -> c.show(tabPane, event.getScreenX(), event.getScreenY()));
                                } else {
                                   Platform.runLater(new Runnable() {
                                       @Override
                                       public void run() {
                                           if (tabPane.getSelectionModel().getSelectedItem() == newTab) {
                                               if (tabPane.getTabs().size() == 1) {
                                                   stage.close();
                                                   Liz3Web.liz3Web.getWindows().remove(this);
                                                   return;
                                               }
                                               newTab(null, BrowserType.HEAVYWEIGHT);
                                           }
                                       }
                                   });
                                }

                            } catch (InterruptedException e) {

                            }
                        }
                    }).start();

                }
            }
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == newTab && tabPane.getTabs().size() == 1) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stage.close();
                        tabPane = null;
                        tabs = null;
                        fxTabs = null;
                        stage = null;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Liz3Web.liz3Web.getWindows().remove(this);
                            }
                        }).start();
                    }
                });
            }

        });
    }
    public FXTab newFXTab(String url) {

        Tab t = new Tab("New Tab: WebKit");
        FXTab fxTab;
        WebView webView = new WebView();
        BorderPane p = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            p = loader.load(this.getClass().getResourceAsStream("/BrowserTab.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BrowserTabController c = loader.getController();
        fxTab = new FXTab(t, c, webView, this);
        p.setCenter(webView);
        t.setContent(p);

        this.fxTabs.put(t, fxTab);
        this.tabPane.getTabs().add(t);
        tabPane.getTabs().remove(newTab);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(t);
        if(url != null) webView.getEngine().load(url);


        return fxTab;
    }
    public void newTab(String url, BrowserType type) {

        Liz3Web.liz3Web.debug("New tab request on: Window: " + this.toString(), 2);
        Thread creator = new Thread(() -> {

            Liz3Web.liz3Web.debug("Creating JavaFX tab", 3);
            Tab tab = new Tab("New Tab: " + type.toString());
            Liz3Web.liz3Web.debug("Setting tab to closable", 3);
            tab.setClosable(true);

            Liz3Web.liz3Web.debug("Creating Chromium Browser", 3);
            Browser browser = new Browser(type);
            Liz3Web.liz3Web.debug("Creating Chromium Browser view for Browser" + browser.toString(), 3);
            BrowserView view = new BrowserView(browser);
            Liz3Web.liz3Web.debug("Creating Browser gui FXMLLoader instance", 3);
            FXMLLoader loader = new FXMLLoader();
            BorderPane pane = null;
            try {
                Liz3Web.liz3Web.debug("Loading Browser Tab gui from FXMLLoader: " + loader.toString(), 3);
                pane = loader.load(BrowserWindow.this.getClass().getResourceAsStream("/BrowserTab.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
                Liz3Web.liz3Web.debug("ERROR while loading gui from FXMLLoader: " + loader.toString(), 1);

            }
            Liz3Web.liz3Web.debug("Getting Controller from FXMLLoader" + loader.toString(), 3);
            BrowserTabController c = loader.getController();
            Liz3Web.liz3Web.debug("Creating BrowserTab instance", 3);
            BrowserTab t = new BrowserTab(tab, browser, view, pane, c, this);
            tabs.put(tab, t);
            Liz3Web.liz3Web.debug("merging Browser: " + browser.toString() + ", Tab: " + tab.toString() +
                    ", BrowserView: " + view.toString() + "into window: " + BrowserWindow.this.toString(), 3);
            BorderPane finalPane = pane;
            finalPane.setCenter(view);
            tab.setContent(finalPane);
            Platform.runLater(() -> {
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);

                Liz3Web.liz3Web.debug("Removing and adding the + Tab from window: " + BrowserWindow.this.toString(), 3);
                tabPane.getTabs().remove(newTab);
                tabPane.getTabs().add(newTab);
                Liz3Web.liz3Web.debug("New Tab created!", 0);

                if (url != null) {
                    Liz3Web.liz3Web.debug("Browser[" + browser.toString() + "] loading url: " + url, 3);
                    browser.loadURL(url);

                } else {
                    c.getUrlBar().requestFocus();
                }
            });


        });
        creator.setPriority(Thread.MAX_PRIORITY);
        creator.start();

    }

    public HashMap<Tab, BrowserTab> getTabs() {
        return tabs;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public HashMap<Tab, FXTab> getFxTabs() {
        return fxTabs;
    }

    public Stage getStage() {
        return stage;
    }

    public BorderPane getPane() {
        return pane;
    }
}

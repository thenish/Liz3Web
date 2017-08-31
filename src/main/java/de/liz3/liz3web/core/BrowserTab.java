package de.liz3.liz3web.core;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.FullScreenHandler;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.Liz3Web;
import de.liz3.liz3web.browserinc.BookMarkEntry;
import de.liz3.liz3web.gui.DownloadManager;
import de.liz3.liz3web.gui.Liz3WebContextMenu;
import de.liz3.liz3web.gui.MainMenu;
import de.liz3.liz3web.gui.controller.BrowserTabController;
import de.liz3.liz3web.gui.controller.FavoriteAddController;
import de.liz3.liz3web.util.extension.ExtensionExecutor;
import de.liz3.liz3web.util.extension.ExtensionManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;

/**
 * Created by yannh on 15.04.2017.
 */
public class BrowserTab {

    private BrowserWindow window;
    private Tab tab;
    private Browser browser;
    private BrowserView browserView;
    private Parent parent;
    private BrowserTabController controller;
    private BrowserSource source;
    private DownloadManager downloadManager;
    private Liz3WebContextMenu contextMenuHandler;

    private JFXTextField urlBar;
    private JFXButton forBtn;
    private JFXButton backBtn;

    private boolean fullscreen;
    private Stage fullscreenStage;
    private Node oldScene;

    public BrowserTab(Tab tab, Browser browser, BrowserView browserView, Parent parent, BrowserTabController c, BrowserWindow window) {
        this.tab = tab;
        this.browser = browser;
        this.browserView = browserView;
        this.parent = parent;
        this.urlBar = c.getUrlBar();
        this.forBtn = c.getForBtn();
        this.backBtn = c.getBackBtrn();
        this.window = window;
        this.controller = c;
        downloadManager = new DownloadManager(this);
        contextMenuHandler = new Liz3WebContextMenu(this);
        setup();
    }

    public void browseTo(String url) {

        if (url.startsWith("locals://")) {
            LocalHandler.handle(this, url);

            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            if (!url.contains(" ") && url.contains(".")) {
                url = "http://" + url;
            } else {
                try {
                    url = "google.com/search?q=" + URLEncoder.encode(url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
        this.browser.loadURL(url);

    }

    public void setBookMarks() {
        HBox list = controller.getBookMarkBar();

        List<JFXButton> btns = new Vector<>();
        if (!Liz3Web.liz3Web.getGeneratedToolBar().isEmpty()) {
            Platform.runLater(() -> {
                list.getChildren().clear();
                for (String key : Liz3Web.liz3Web.getGeneratedToolBar().keySet()) {


                    JFXButton cl = Liz3Web.liz3Web.getGeneratedToolBar().get(key);
                    ImageView view = (ImageView) cl.getGraphic();
                    Image img = null;
                    if (view != null) img = view.getImage();
                    JFXButton b = new JFXButton(cl.getText());
                    if (img != null) {
                        ImageView newView = new ImageView(img);
                        newView.setFitWidth(15);
                        newView.setFitHeight(15);
                        b.setGraphic(newView);
                    }
                    if (cl.getTooltip() != null) {
                        b.setTooltip(new Tooltip(cl.getTooltip().getText()));
                    }


                    b.setOnAction(event -> browser.loadURL(key));
                    btns.add(b);
                }
                list.getChildren().addAll(btns);
            });
            return;
        }

        Platform.runLater(() -> list.getChildren().clear());
        Liz3Web.liz3Web.getGeneratedToolBar().clear();
        for (BookMarkEntry e : Liz3Web.liz3Web.getFavoriteManager().getEntries().values()) {

            String forFav;
            String orgHost = e.getUrl().split("/")[2];
            if (e.getUrl().startsWith("https://")) {
                forFav = "https://" + orgHost + "/favicon.ico";
            } else {
                forFav = "http://" + orgHost + "/favicon.ico";

            }
            ImageView view = Liz3Web.liz3Web.getFavIconGetter().getPicture(forFav, e.getId());
            if (view != null) {
                System.out.println("Not null for: " + e.getUrl());
            } else {
                System.out.println("Null for: " + e.getUrl());

            }
            JFXButton b = null;
            if (e.getName() == null || e.getName().equalsIgnoreCase("")) {
                String x = e.getUrl().split("/")[2].substring(0, 1);
                if (view == null) {
                    b = new JFXButton(x);
                } else {
                    b = new JFXButton(null);
                    b.setGraphic(view);
                }
                b.setTooltip(new Tooltip(e.getUrl()));
            } else {
                String x = null;
                if (e.getName().length() < 15) {
                    x = e.getName();
                } else {
                    x = e.getName().substring(0, 15);
                }
                if (view == null) {
                    b = new JFXButton(x);
                } else {
                    b = new JFXButton(null);
                    b.setGraphic(view);

                }
                b.setTooltip(new Tooltip(e.getName() + " - " + e.getUrl()));
            }
            Liz3Web.liz3Web.getGeneratedToolBar().put(e.getUrl(), b);
            b.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {

                    return;
                }
                browseTo(e.getUrl());
            });
            JFXButton finalB = b;
            Platform.runLater(() -> list.getChildren().add(finalB));
        }
    }

    private void setupExtensions() {

        ExtensionManager ex = Liz3Web.liz3Web.getExtensionManager();

        for (ExtensionExecutor executor : ex.getExtensions()) {
            JFXButton btn = new JFXButton(executor.getExtension().getName().substring(0, 1));
            btn.prefWidth(16);
            btn.prefHeight(16);
            controller.getExtensionBar().getChildren().add(btn);
            btn.setOnMouseClicked(event -> {
                if(executor.getVisible()) return;
                executor.setVisible(true);

                Stage st = executor.getPopUp(window.getStage(), browser1 -> {


                    browser1.loadURL("file://" + executor.getExtension().getPopUp().getHtmlFile());

                    System.out.println(browser1.getRemoteDebuggingURL());

                    return null;
                });
                st.show();
            });
        }

    }

    private void setup() {
        Liz3Web.liz3Web.debug("Setup started: " + BrowserTab.this.toString(), 2);
        Liz3Web.liz3Web.debug("Setting Bookmarks, BrowserTab: " + BrowserTab.this.toString(), 3);

        Liz3Web.liz3Web.debug("Starting loading of extensions", 2);
        setupExtensions();
        new Thread(this::setBookMarks).start();
        contextMenuHandler.setHandler();
        browser.setDownloadHandler(downloadItem -> {
            new Thread(() -> downloadManager.handleFile(downloadItem)).start();
            return true;
        });
        this.browser.setPopupHandler(popupParams -> {
            Platform.runLater(() -> window.newTab(popupParams.getURL(), BrowserType.HEAVYWEIGHT));
            return null;
        });
        controller.getMenuBtn().setOnMouseClicked(event -> {
            ContextMenu menu = MainMenu.getMenu(BrowserTab.this);
            menu.show(parent, event.getScreenX(), event.getScreenY());
        });
        backBtn.setOnAction(event -> {
            if (browser.canGoBack()) browser.goBack();
        });
        forBtn.setOnAction(event -> {
            if (browser.canGoForward()) browser.goForward();
        });
        urlBar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                browseTo(urlBar.getText());
            }
        });
        controller.getReloadBtn().setOnAction(event -> browser.reload());
        browser.setFullScreenHandler(new FullScreenHandler() {
            @Override
            public void onFullScreenEnter() {
                Platform.runLater(() -> fullscreen());
            }

            @Override
            public void onFullScreenExit() {
                Platform.runLater(() -> fullscreen());
            }
        });
        browserView.setOnKeyPressed(event -> {


            if (event.getCode() == KeyCode.D) {

                if (event.isControlDown()) {

                    Platform.runLater(() -> {
                        Popup popup = new Popup();

                        FXMLLoader loader = new FXMLLoader();
                        Parent r = null;
                        try {
                            r = loader.load(BrowserTab.this.getClass().getResourceAsStream("/FavoriteAdd.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        r.setStyle("-fx-background-color: white");
                        FavoriteAddController c = loader.getController();
                        c.getNameField().setText(browser.getTitle());
                        c.getLinkField().setText(browser.getURL());
                        c.getAddBtn().setOnAction(event1 -> {

                                    Liz3Web.liz3Web.getFavoriteManager().add(c.getLinkField().getText(), c.getNameField().getText());
                                    Liz3Web.liz3Web.getGeneratedToolBar().clear();
                                    new Thread(() -> {
                                        setBookMarks();
                                        for (BrowserWindow win : Liz3Web.liz3Web.getWindows()) {
                                            for (BrowserTab tab : win.getTabs().values()) {
                                                if (tab == this) {
                                                    continue;
                                                }
                                                new Thread(tab::setBookMarks).start();
                                            }
                                            for (FXTab tab : win.getFxTabs().values()) {
                                                tab.setBookMarks();
                                            }
                                        }
                                    }).start();
                                    popup.hide();
                                }
                        );
                        c.getCancelBtn().setOnAction(event12 -> popup.hide());
                        popup.getContent().add(r);

                        popup.show(browserView.getScene().getWindow());
                    });

                }
            }
            if (event.getCode() == KeyCode.F11) {
                fullscreen();
            }
            if (event.getCode() == KeyCode.F5) {

                new Thread(() -> {
                    if (event.isControlDown()) {

                        CookieStorage storage = browser.getCookieStorage();
                        browser.getCacheStorage().clearCache();
                        storage.getAllCookies().clear();
                    }
                    BrowserTab.this.browser.reload();
                }).start();
            }
        });
        this.browser.addLoadListener(new LoadListener() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {

                Liz3Web.liz3Web.debug("Start loading in BrowserTab: " + BrowserTab.this.toString(), 2);
                Liz3Web.liz3Web.getHistory().addEntry(BrowserTab.this.browser.getURL());
                String url = browser.getURL();

                Platform.runLater(() -> {

                    urlBar.setText(url);
                    controller.getReloadBtn().setText("S");
                    controller.getReloadBtn().setOnAction(event -> browser.stop());
                });
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {

                Liz3Web.liz3Web.debug("Provisional loading in BrowserTab: " + BrowserTab.this.toString(), 3);
                String url = browser.getURL();
                String n = browser.getTitle();
                Platform.runLater(() -> {

                    urlBar.setText(url);
                    tab.setText(n);

                });

            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
                Liz3Web.liz3Web.debug("Finished loading in BrowserTab: " + BrowserTab.this.toString(), 2);
                String url = browser.getURL();
                String n = browser.getTitle();
                Platform.runLater(() -> {
                    urlBar.setText(url);
                    tab.setText(n);
                    controller.getReloadBtn().setText("R");
                });
                controller.getReloadBtn().setOnAction(event -> browser.reload());

            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {

                Liz3Web.liz3Web.debug("Document load failed in BrowserTab: " + BrowserTab.this.toString(), 3);

            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {

                Liz3Web.liz3Web.debug("Document loaded in Frame for BrowserTab: " + BrowserTab.this.toString(), 3);
                String url = browser.getURL();
                String n = browser.getTitle();
                Platform.runLater(() -> {

                    urlBar.setText(url);
                    tab.setText(n);

                });
            }

            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {
                Liz3Web.liz3Web.debug("Document loaded in Mainframe for BrowserTab: " + BrowserTab.this.toString(), 3);
            }
        });
        tab.setOnCloseRequest(event -> {
            window.getTabs().remove(tab);
            if (source != null && source.getStage().isShowing()) {
                source.getStage().close();
            }
            new Thread(() -> {
                browser.stop();
                browser.dispose();
            }).start();

        });
    }

    public void source() {

        if (source == null) {
            source = new BrowserSource();
        }
        if (source.getStage() == null || !source.getStage().isShowing()) {
            source.show(browser);
        }
    }

    private void fullscreen() {

        BorderPane rootStage = window.getPane();
        if (fullscreen) {
            fullscreen = false;

            BorderPane r = (BorderPane) tab.getContent();
            r.setCenter(browserView);
            rootStage.setCenter(oldScene);
            // window.getStage().setFullScreen(false);
            return;
        }
        fullscreen = true;
        oldScene = rootStage.getCenter();
        rootStage.setCenter(browserView);
        if (!window.getStage().isFullScreen()) {
            window.getStage().setFullScreen(true);

        }

    }

    public Tab getTab() {
        return tab;
    }

    public Browser getBrowser() {
        return browser;
    }

    public BrowserView getBrowserView() {
        return browserView;
    }

    public Parent getParent() {
        return parent;
    }

    public JFXTextField getUrlBar() {
        return urlBar;
    }

    public JFXButton getForBtn() {
        return forBtn;
    }

    public JFXButton getBackBtn() {
        return backBtn;
    }

    public BrowserWindow getWindow() {
        return window;
    }

    public void setWindow(BrowserWindow window) {
        this.window = window;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public BrowserTabController getController() {
        return controller;
    }

    public BrowserSource getSource() {
        return source;
    }

    public Stage getFullscreenStage() {
        return fullscreenStage;
    }
}

package de.liz3.liz3web.browser;

import com.jfoenix.controls.JFXTextField;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.FullScreenHandler;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.gui.GuiManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yannh on 29.11.2016.
 */
public class BrowserTab {

    private Parent sourceParent;
    private Stage sourceStage;
    private Scene sourceScene;
    private boolean sourceVisible = false;
    private Stage fullscreenStage;
    private String currentTitle;
    private String currentUrl;
    private Browser browser;
    private BrowserView browserView;
    private Tab tab;
    private JFXTextField urlBar;
    private BrowserSource source;
    private BorderPane pane;
    private boolean fullscreen;

    public BrowserTab(Browser browser, BrowserView view, Tab tab, JFXTextField urlBar) {

        this.browser = browser;
        this.browserView = view;
        this.tab = tab;
        this.urlBar = urlBar;
        this.pane = pane;
        source = new BrowserSource();

        browser.setFullScreenHandler(new FullScreenHandler() {
            @Override
            public void onFullScreenEnter() {

             Platform.runLater(() -> {
                 
                 BorderPane pane = new BorderPane();
                 GuiManager.rootStage.hide();
                 Stage stage = new Stage();
                 pane.setCenter(browserView);

                 stage.setScene(new Scene(pane));

                 stage.setFullScreen(true);
                 stage.show();
                 fullscreen = true;
                 fullscreenStage = stage;
                 tab.setContent(null);
             });
            }

            @Override
            public void onFullScreenExit() {

             Platform.runLater(() -> {
                 fullscreenStage.close();
                 GuiManager.rootStage.show();
                 fullscreen = false;
                 fullscreenStage = null;
                 tab.setContent(view);
             });
            }
        });
        browserView.setOnKeyReleased(event -> {

            if(event.getCode() == KeyCode.ESCAPE) {

                if(fullscreen) {
                    Platform.runLater(() -> {
                        fullscreenStage.close();
                        GuiManager.rootStage.show();
                        fullscreen = false;
                        fullscreenStage = null;
                        tab.setContent(view);
                    });
                }
            }
            if(event.getCode() == KeyCode.F11) {

                if(!fullscreen) {
                    BorderPane pane = new BorderPane();
                    GuiManager.rootStage.hide();
                    Stage stage = new Stage();
                    pane.setCenter(browserView);

                    stage.setScene(new Scene(pane));

                    stage.setFullScreen(true);
                    stage.show();
                    fullscreen = true;
                    fullscreenStage = stage;
                    tab.setContent(null);
                } else {
                    fullscreenStage.close();
                    GuiManager.rootStage.show();
                    fullscreen = false;
                    fullscreenStage = null;
                    tab.setContent(view);
                }
            }
        });
        browser.setDownloadHandler(new DownloadHandler() {

            @Override
            public boolean allowDownload(DownloadItem downloadItem) {


                GuiManager.browserController.popUpDownloadFrame(downloadItem);


                return true;
            }
        });
        this.tab.setOnCloseRequest(event -> {

            browser.dispose();
            GuiManager.openTabs.remove(BrowserTab.this);

        });
        this.browser.setPopupHandler(popupParams -> {


            Platform.runLater(() -> GuiManager.manager.insertNewTab(popupParams.getURL()));

            return null;
        });
        this.browser.addLoadListener(new LoadListener() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {

                GuiManager.history.addEntry(BrowserTab.this.browser.getURL());
                Platform.runLater(() -> {
                    BrowserTab.this.currentTitle = BrowserTab.this.browser.getURL();
                    BrowserTab.this.currentUrl = BrowserTab.this.browser.getURL();
                    Tab active = GuiManager.currentActive;

                    if (active == BrowserTab.this.tab) {
                        BrowserTab.this.urlBar.setText(BrowserTab.this.browser.getURL());
                    }

                });
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {

            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {

                Tab active = GuiManager.currentActive;

                if (active == BrowserTab.this.tab) {

                    GuiManager.history.addEntry(BrowserTab.this.browser.getURL());

                    Platform.runLater(() -> {
                        BrowserTab.this.tab.setText(BrowserTab.this.browser.getTitle());
                        BrowserTab.this.currentTitle = BrowserTab.this.browser.getTitle();
                        BrowserTab.this.urlBar.setText(BrowserTab.this.browser.getURL());
                    });
                }




            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {


            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {

            }

            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {

            }
        });
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public Tab getTab() {
        return tab;
    }

    public Parent getSourceParent() {
        return sourceParent;
    }

    public Stage getSourceStage() {
        return sourceStage;
    }

    public Scene getSourceScene() {
        return sourceScene;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void openDebugger() {

        source.show(this.browser);
    }

    public void dispose() {

        this.browser.dispose();


    }

    public void initSourceView() {

        FXMLLoader loader = new FXMLLoader();


        try {
            BrowserTab.this.sourceParent = loader.load(getClass().getResourceAsStream("/com/liz3/browser/recs/PageSource.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BrowserTab.this.sourceStage = new Stage();
        BrowserTab.this.sourceScene = new Scene(sourceParent, 1000, 640);

        BrowserTab.this.sourceStage.setScene(sourceScene);

        BrowserTab.this.sourceStage.setTitle(BrowserTab.this.currentTitle);

        BrowserTab.this.sourceStage.show();


        this.sourceVisible = true;
        Platform.runLater(() -> {


        });

    }

    public void browseTo(String url) {

        if(url.startsWith("locals://")) {
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

    public void goBack() {

        if (this.browser.canGoBack()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    BrowserTab.this.browser.goBack();
                }
            });
        }
    }

    public void goFoward() {

        if (this.browser.canGoForward()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    BrowserTab.this.browser.goForward();
                }
            });
        }

    }


    public BrowserSource getSource() {
        return source;
    }

    public BorderPane getPane() {
        return pane;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }
}

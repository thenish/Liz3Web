package de.liz3.liz3web.browser;

import com.jfoenix.controls.JFXTextField;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.gui.GuiManager;
import javafx.application.Platform;
import javafx.scene.control.Tab;

/**
 * Created by yannh on 29.11.2016.
 */
public class BrowserTab {

    private String currentTitle;
    private String currentUrl;
    private Browser browser;
    private BrowserView browserView;
    private Tab tab;
    private JFXTextField urlBar;

    public String getCurrentTitle() {
        return currentTitle;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public Tab getTab() {
        return tab;
    }

    public BrowserTab(Browser browser, BrowserView view, Tab tab, JFXTextField urlBar) {

        this.browser = browser;
        this.browserView = view;
        this.tab = tab;
        this.urlBar = urlBar;


        this.tab.setOnCloseRequest(event -> {

            browser.dispose();
            GuiManager.openTabs.remove(BrowserTab.this);

        });
        this.browser.setPopupHandler(popupParams -> {


            Platform.runLater(() -> GuiManager.mainManager.insertNewTab(popupParams.getURL()));

            return null;
        });
        this.browser.addLoadListener(new LoadListener() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {

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

                Platform.runLater(() -> {
                    BrowserTab.this.tab.setText(BrowserTab.this.browser.getTitle());
                    BrowserTab.this.currentTitle = BrowserTab.this.browser.getTitle();
                });


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

    public void dispose() {

        this.browser.dispose();


    }
    public void browseTo(String url) {

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        this.browser.loadURL(url);

    }

    public void goBack() {

        if (this.browser.canGoBack()) {
            this.browser.goBack();
        }
    }

    public void goFoward() {

        if (this.browser.canGoForward()) {
            this.browser.goForward();
        }

    }


}

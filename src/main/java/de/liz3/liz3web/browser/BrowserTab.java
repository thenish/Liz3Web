package de.liz3.liz3web.browser;

import com.jfoenix.controls.JFXTextField;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.dom.DOMNode;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.gui.GuiManager;
import de.liz3.liz3web.gui.controller.PageSourceController;
import de.liz3.liz3web.util.HttpMethods;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by yannh on 29.11.2016.
 */
public class BrowserTab {

    private Parent sourceParent;
    private Stage sourceStage;
    private Scene sourceScene;
    private boolean sourceVisible = false;


    private String currentTitle;
    private String currentUrl;
    private Browser browser;
    private BrowserView browserView;
    private Tab tab;
    private JFXTextField urlBar;
    private DOMSource source;

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
        PageSourceController con = loader.getController();
        BrowserTab.this.sourceStage.show();
        con.initAsLoading();

        this.sourceVisible = true;
        Platform.runLater(() -> {


        });

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

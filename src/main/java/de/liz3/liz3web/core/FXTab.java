package de.liz3.liz3web.core;

import com.jfoenix.controls.JFXButton;
import com.teamdev.jxbrowser.chromium.BrowserType;
import de.liz3.liz3web.Liz3Web;
import de.liz3.liz3web.browserinc.BookMarkEntry;
import de.liz3.liz3web.gui.controller.BrowserTabController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yannh on 16.04.2017.
 */
public class FXTab {

    private BrowserWindow window;
    private Tab tab;
    private BrowserTabController c;
    private WebView webView;
    private WebEngine engine;

    public FXTab(Tab tab, BrowserTabController c, WebView webView,  BrowserWindow win) {
        this.tab = tab;
        this.c = c;
        this.webView = webView;
        engine = webView.getEngine();
        this.window = win;

        setup();
    }

    public void browseTo(String url) {

        if (url.startsWith("locals://")) {
            //LocalHandler.handle(this, url);

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
        engine.load(url);

    }
    public void setBookMarks() {

        HBox list = c.getBookMarkBar();
        list.getChildren().clear();

        for (BookMarkEntry e : Liz3Web.liz3Web.getFavoriteManager().getEntries().values()) {

            JFXButton b = null;
            if (e.getName() == null || e.getName().equalsIgnoreCase("")) {
                String x = e.getUrl().split("/")[2].substring(0, 1);
                b = new JFXButton(x);
                b.setTooltip(new Tooltip(e.getUrl()));
            } else {
                String x = null;
                if (e.getName().length() < 15) {
                    x = e.getName();
                } else {
                    x = e.getName().substring(0, 15);
                }
                b = new JFXButton(x);
                b.setTooltip(new Tooltip(e.getName() + " - " + e.getUrl()));
            }

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
    private void setup() {
        setBookMarks();
        c.getBackBtrn().setOnAction(event -> {
            goBack();
        });
        c.getForBtn().setOnAction(event -> goForward());
        c.getUrlBar().setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                browseTo(c.getUrlBar().getText());
            }
        });
        tab.setOnCloseRequest(event -> window.getFxTabs().remove(tab));
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {

                if(newValue == Worker.State.SCHEDULED) {
                    c.getReloadBtn().setText("S");
                    c.getReloadBtn().setOnAction(event -> engine.getLoadWorker().cancel());
                }
                if(newValue == Worker.State.RUNNING) {

                }
                if(newValue == Worker.State.SUCCEEDED) {
                    Liz3Web.liz3Web.getHistory().addEntry(engine.getLocation());
                    tab.setText(engine.getTitle());
                    c.getReloadBtn().setText("R");
                    c.getReloadBtn().setOnAction(event -> engine.reload());
                }

            }
        });

    }

    public void goBack()
    {
        final WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        Platform.runLater(() ->
        {
            history.go(entryList.size() > 1 && currentIndex > 0 ? -1 : 0);
        });
    }

    public void goForward()
    {
        final WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        Platform.runLater(() ->
        {
            history.go(entryList.size() > 1 && currentIndex < entryList.size() - 1 ? 1 : 0);
        });
    }
    public Tab getTab() {
        return tab;
    }

    public BrowserTabController getC() {
        return c;
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getEngine() {
        return engine;
    }

    public BrowserWindow getWindow() {
        return window;
    }

    public void setWindow(BrowserWindow window) {
        this.window = window;
    }
}

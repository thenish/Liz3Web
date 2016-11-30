package de.liz3.liz3web.gui;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.Main;
import de.liz3.liz3web.browser.BrowserTab;
import de.liz3.liz3web.gui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

/**
 * Created by yannh on 29.11.2016.
 */
public class GuiManager extends Application {

    public static boolean isFullScreen;
    public static MainController browserController;
    public static GuiManager mainManager;

    public static Parent rootParent;
    public static Scene rootScene;
    public static ArrayList<BrowserTab> openTabs = new ArrayList<>();
    public static Tab currentActive;


    public static void setupGui() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        mainManager = this;
        Main.dPrint("Loading gui...");
        Main.dPrint("Loading fxml");


        FXMLLoader loader = new FXMLLoader();
        rootParent = loader.load(getClass().getResourceAsStream("/guixml/MainWindow.fxml"));

        if (rootParent != null) {

            Main.dPrint("Setting up the Window");

            rootScene = new Scene(rootParent);
            primaryStage.setScene(rootScene);
            primaryStage.sizeToScene();
            primaryStage.setTitle("Liz3Web");
            Main.dPrint("Hooking gui to Control");
            browserController = loader.getController();
            browserController.setUp();
            primaryStage.setOnCloseRequest(event -> Platform.exit());

            Main.dPrint("Showing the window");
            primaryStage.show();
            primaryStage.toFront();

        } else {
            if (Main.isDebug) {
                throw new Exception("Fail while loading the main fxml file");
            }
            System.exit(1);
        }

    }

    @Override
    public void stop() {

        for(BrowserTab tab : openTabs) {

            tab.dispose();
        }

    }

    public void insertNewTab(String url) {


        Browser newBrowser = new Browser();
        BrowserView newView = new BrowserView(newBrowser);
        Tab tab = new Tab();
        tab.setContent(newView);

        openTabs.add(new BrowserTab(newBrowser, newView, tab, browserController.urlField));

        browserController.tabPane.getTabs().remove(browserController.newTab);
        browserController.tabPane.getTabs().add(tab);
        browserController.tabPane.getSelectionModel().select(tab);
        browserController.tabPane.getTabs().add(browserController.newTab);

        currentActive = tab;


        Task<Runnable> create = new Task<Runnable>() {
            @Override
            protected Runnable call() throws Exception {

                Platform.runLater(() -> {



                    newBrowser.loadURL(url);

                });

                return null;
            }
        };
        new Thread(create).start();


    }
}


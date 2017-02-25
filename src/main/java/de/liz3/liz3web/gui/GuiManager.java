package de.liz3.liz3web.gui;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import de.liz3.liz3web.Main;
import de.liz3.liz3web.browser.BrowserTab;
import de.liz3.liz3web.browser.History;
import de.liz3.liz3web.browser.TabManager;
import de.liz3.liz3web.gui.controller.MainController;
import de.liz3.liz3web.util.CommandLineWorker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by yannh on 29.11.2016.
 */
public class GuiManager extends Application {

    public static boolean isFullScreen;
    public static MainController browserController;
    public static GuiManager mainManager;
    public static TabManager manager;
    public static Parent rootParent;
    public static Scene rootScene;
    public static ArrayList<BrowserTab> openTabs = new ArrayList<>();
    public static Tab currentActive;
    public static CommandLineWorker cmdWorker;
    public static History history;


    public static void setupGui() {
        BrowserPreferences.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Liz3Web/1.0");
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        history = new History();
        history.loadIn();
        mainManager = this;
        Main.dPrint("Loading gui...");
        Main.dPrint("Loading fxml");


        FXMLLoader loader = new FXMLLoader();
        rootParent = loader.load(getClass().getResourceAsStream("/MainWindow.fxml"));

        if (rootParent != null) {

            cmdWorker = new CommandLineWorker();

            Main.dPrint("Setting up the Window");

            rootScene = new Scene(rootParent);
            rootScene.getStylesheets().add("style/AppStyle.css");
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

        System.exit(0);
    }

}


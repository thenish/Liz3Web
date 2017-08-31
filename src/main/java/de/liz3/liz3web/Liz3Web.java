package de.liz3.liz3web;

import com.jfoenix.controls.JFXButton;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import de.liz3.liz3web.browserinc.FavoriteManager;
import de.liz3.liz3web.browserinc.History;
import de.liz3.liz3web.core.BrowserWindow;
import de.liz3.liz3web.core.BrowserSettings;
import de.liz3.liz3web.util.ClientSocket;
import de.liz3.liz3web.util.FavIconGetter;
import de.liz3.liz3web.util.extension.ExtensionManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by yannh on 15.04.2017.
 */
public class Liz3Web extends Application {

    public static Liz3Web liz3Web;
    private Vector<BrowserWindow> windows;
    private int debugLevel;
    private History history;
    private BrowserSettings settings;
    private FavoriteManager favoriteManager;
    private FavIconGetter favIconGetter;
    private ClientSocket clientSocket;
    private HashMap<String, JFXButton> generatedToolBar;
    private ExtensionManager extensionManager;

    public Liz3Web() {


        debug("Starting...", 0);
        debug("init..", 3);
        debug("setting static vars", 3);
        liz3Web = this;
        windows = new Vector<>();
        settings = new BrowserSettings();
        generatedToolBar = new HashMap<>();
        try {
            extensionManager = new ExtensionManager(new File(new File(".").getCanonicalPath(), "extensions"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            history = new History(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        history.loadIn();
        try {
            favoriteManager = new FavoriteManager(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        favoriteManager.loadIn();
        try {
            favIconGetter = new FavIconGetter(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientSocket = new ClientSocket();
        clientSocket.startSocket();

        debug("Setting Chromium preferences...", 2);

    }



    public static void run(String[] args) {

        BrowserPreferences.setUserAgent("Mozilla/5.0 (" + System.getProperty("os.name") + ") AppleWebKit/537.36 (KHTML, like Gecko) Liz3Web/1.0 Runtime/55.0.2883.87");
        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9223");
        BrowserPreferences.setChromiumVariable("GOOGLE_API_KEY", "AIzaSyBXo9GdKdTjWi6X_ZK0wyh8OfiIJLDc3yg");
        BrowserPreferences.setChromiumVariable("GOOGLE_DEFAULT_CLIENT_ID","347102639012-mfih61u3run5qkvfslr76f86k6e4gdkh.apps.googleusercontent.com");
        BrowserPreferences.setChromiumVariable("GOOGLE_DEFAULT_CLIENT_SECRET","qgU9nMYuTYuE3c0ahtXbqbIk");

       if(Environment.isMac()) BrowserCore.initialize();



       launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        Parameters parameters = getParameters();
        for(String x : parameters.getRaw()) {
            if(x.startsWith("-debug=")) {
                debugLevel = Integer.valueOf(x.split("=")[1]);
            }
        }
       debug("Starting gui...", 0);
        debug("Creating first window instance: ", 3);
        BrowserWindow window = new BrowserWindow(primaryStage);
        debug("Instance is: " + window.toString(), 3);
        windows.add(window);
        debug("Creating first tab", 2);
        window.newTab(null, BrowserType.HEAVYWEIGHT);
    }

    public void debug(String message, int debugLevel) {

        if (debugLevel <= this.debugLevel) {
            System.out.println("Liz3Web: [" + getFromDebug(debugLevel) + "]: " + message);
        }

    }

    private String getFromDebug(int level) {

        switch (level) {
            case 0:
                return "DEFAULT";
            case 1:
                return "ERROR";
            case 2:
                return "INFO";
            case 3:
                return "SERVE";
            default:
                return "DEFAULT";
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public Vector<BrowserWindow> getWindows() {
        return windows;
    }

    public History getHistory() {
        return history;
    }

    public BrowserSettings getSettings() {
        return settings;
    }

    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }

    public FavIconGetter getFavIconGetter() {
        return favIconGetter;
    }

    public HashMap<String, JFXButton> getGeneratedToolBar() {
        return generatedToolBar;
    }

    public void setGeneratedToolBar(HashMap<String, JFXButton> generatedToolBar) {
        this.generatedToolBar = generatedToolBar;
    }

    public ExtensionManager getExtensionManager() {
        return extensionManager;
    }
}

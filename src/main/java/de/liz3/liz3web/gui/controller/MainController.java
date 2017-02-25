package de.liz3.liz3web.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.Main;
import de.liz3.liz3web.browser.BrowserTab;
import de.liz3.liz3web.browser.TabManager;
import de.liz3.liz3web.gui.GuiManager;
import de.liz3.liz3web.gui.menus.MainMenu;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * Created by yannh on 29.11.2016.
 */
public class MainController {

    @FXML
    public TabPane tabPane;

    @FXML
    public JFXButton backBtn;

    @FXML
    public JFXButton forwardBtn;

    @FXML
    public MenuButton mainMenu;

    @FXML
    public JFXTextField urlField;

    @FXML
    public Tab newTab;

    @FXML
    public BorderPane rootPane;


    public void setUp() {

        GuiManager.manager = new TabManager();
        GuiManager.manager.setController(this);
        Main.dPrint("Setting up gui elements");
        MainMenu.createMenu(mainMenu);
        tabPane.getTabs().remove(newTab);
        Browser fBrowser = new Browser();
        BrowserView fview = new BrowserView(fBrowser);
        Tab fTab = new Tab();
        fTab.setContent(fview);
        tabPane.getTabs().add(fTab);
        GuiManager.openTabs.add(new BrowserTab(fBrowser, fview, fTab, urlField));
        tabPane.getTabs().add(newTab);
        GuiManager.currentActive = fTab;
        fBrowser.loadURL("https://google.de");

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {

                if (newValue == newTab) {

                    GuiManager.manager.newTab();
                    return;
                }

                GuiManager.currentActive = newValue;
                for (BrowserTab tab : GuiManager.openTabs) {
                    if (tab.getTab() == newValue) {
                        urlField.setText(tab.getCurrentUrl());
                    }
                }
            }
        });

        urlField.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.ENTER) {

                Tab activeTab = tabPane.getSelectionModel().getSelectedItem();

                for (BrowserTab tab : GuiManager.openTabs) {

                    if (tab.getTab().equals(activeTab)) {


                        tab.browseTo(urlField.getText());
                    }
                }


            }
        });
        forwardBtn.setOnAction(event -> {

            Tab active = tabPane.getSelectionModel().getSelectedItem();

            for (BrowserTab tab : GuiManager.openTabs) {

                if (tab.getTab().equals(active)) {

                    tab.goFoward();

                }
            }
        });
        backBtn.setOnAction(event -> {

            Tab active = tabPane.getSelectionModel().getSelectedItem();

            for (BrowserTab tab : GuiManager.openTabs) {

                if (tab.getTab().equals(active)) {

                    tab.goBack();

                }
            }
        });

    }

    public void popUpDownloadFrame(DownloadItem item) {


        item.pause();
        Pane pane = new Pane();
        BorderPane p = GuiManager.browserController.rootPane;

        Label label = new Label();
        JFXButton accept = new JFXButton("Download");
        JFXButton reject = new JFXButton("Reject");
        JFXButton execute = new JFXButton("Execute");



        accept.setOnAction(event -> {

            item.resume();
            p.setBottom(null);
        });
        reject.setOnAction(event -> {

            item.cancel();
            p.setBottom(null);
        });
        accept.setLayoutX(60);
        accept.setLayoutY(30);
        reject.setLayoutX(135);
        reject.setLayoutY(30);
        label.setLayoutX(10);
        label.setLayoutY(10);
        label.setText(item.getURL());
     //   label.setPrefHeight(10);
       // label.setPrefWidth(item.getURL().length() * 2);

        pane.getChildren().addAll(accept, reject, label);

        pane.setPrefWidth(350);
        pane.setPrefHeight(90);

        System.out.println("LoL");
        Platform.runLater(() -> p.setBottom(pane));


    }
}

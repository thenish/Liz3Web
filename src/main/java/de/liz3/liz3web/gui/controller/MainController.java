package de.liz3.liz3web.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.liz3.liz3web.Main;
import de.liz3.liz3web.browser.BrowserTab;
import de.liz3.liz3web.gui.GuiManager;
import de.liz3.liz3web.gui.menus.MainMenu;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
    public JFXTextField searchField;

    @FXML
    public Tab newTab;


    public void setUp() {

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

                    tabPane.getTabs().remove(newTab);
                    Tab tab = new Tab("new tab");
                    Browser browser = new Browser();
                    BrowserView newView = new BrowserView(browser);
                    tab.setContent(newView);
                    tabPane.getTabs().add(tab);
                    GuiManager.openTabs.add(new BrowserTab(browser, newView, tab, urlField));
                    tabPane.getSelectionModel().select(tab);
                    tabPane.getTabs().add(newTab);
                    browser.loadURL("");
                    urlField.setText("about:blank");
                    GuiManager.currentActive = tab;
                    return;
                }

                GuiManager.currentActive = newValue;
                for(BrowserTab tab : GuiManager.openTabs) {
                    if(tab.getTab() == newValue) {
                        urlField.setText(tab.getCurrentUrl());
                    }
                }
            }
        });

        urlField.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.ENTER) {

                Tab activeTab = tabPane.getSelectionModel().getSelectedItem();

                for(BrowserTab tab : GuiManager.openTabs) {

                    if(tab.getTab().equals(activeTab)) {


                        tab.browseTo(urlField.getText());
                    }
                }


            }
        });
        searchField.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.ENTER) {

                Tab activeTab = tabPane.getSelectionModel().getSelectedItem();

                for(BrowserTab tab : GuiManager.openTabs) {

                    if(tab.getTab().equals(activeTab)) {

                        String text = searchField.getText();
                        text.replace(" ", "+");
                        text.replace("+", "%2B");

                        tab.browseTo("http://google.com/search?q=" + text);
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

}

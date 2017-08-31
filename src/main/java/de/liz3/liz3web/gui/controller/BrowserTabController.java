package de.liz3.liz3web.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Created by yannh on 15.04.2017.
 */
public class BrowserTabController {

    public HBox bookMarkBar;
    @FXML
    private BorderPane pane;
    @FXML
    private JFXTextField urlBar;
    @FXML
    private JFXButton menuBtn;
    @FXML
    private JFXButton backBtrn;
    @FXML
    private JFXButton forBtn;
    @FXML
    private JFXButton reloadBtn;
    @FXML
    private HBox extensionBar;


    public HBox getBookMarkBar() {
        return bookMarkBar;
    }

    public BorderPane getPane() {
        return pane;
    }

    public JFXTextField getUrlBar() {
        return urlBar;
    }

    public JFXButton getMenuBtn() {
        return menuBtn;
    }

    public JFXButton getBackBtrn() {
        return backBtrn;
    }

    public JFXButton getForBtn() {
        return forBtn;
    }

    public JFXButton getReloadBtn() {
        return reloadBtn;
    }

    public HBox getExtensionBar() { return extensionBar; }
}

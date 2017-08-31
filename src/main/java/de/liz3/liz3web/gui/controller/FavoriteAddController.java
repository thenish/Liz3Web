package de.liz3.liz3web.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

/**
 * Created by yannh on 17.04.2017.
 */
public class FavoriteAddController {

    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextField linkField;
    @FXML
    private JFXButton addBtn;
    @FXML
    private JFXButton cancelBtn;

    public JFXTextField getNameField() {
        return nameField;
    }

    public JFXTextField getLinkField() {
        return linkField;
    }

    public JFXButton getAddBtn() {
        return addBtn;
    }

    public JFXButton getCancelBtn() {
        return cancelBtn;
    }
}

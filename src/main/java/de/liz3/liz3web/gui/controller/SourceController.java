package de.liz3.liz3web.gui.controller;

import com.jfoenix.controls.JFXTextArea;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;

/**
 * Created by yannh on 22.02.2017.
 */
public class SourceController {

    @FXML
    public TreeView<String> sourceTreeView;
    @FXML
    public JFXTextArea sourceArea;

    private DOMDocument doc;
    private String url;

    public HashMap<String, Runnable> actions;

    public void setup(DOMDocument doc, String url) {


        this.url = url;
        actions = new HashMap<>();
        this.doc = doc;
        TreeItem<String> rootItem = new TreeItem<>("Site");
        actions.clear();

        sourceTreeView.setRoot(rootItem);
        sourceTreeView.getRoot().getChildren().clear();

        sourceTreeView.setOnMouseReleased(event -> {

            if(sourceTreeView.getSelectionModel().getSelectedItem() == null)
                return;
            if(!actions.containsKey(sourceTreeView.getSelectionModel().getSelectedItem().getValue()))
                return;

            actions.get(sourceTreeView.getSelectionModel().getSelectedItem().getValue()).run();

        });
    }



    public TreeView<String> getSourceTreeView() {
        return sourceTreeView;
    }

    public JFXTextArea getSourceArea() {
        return sourceArea;
    }
}

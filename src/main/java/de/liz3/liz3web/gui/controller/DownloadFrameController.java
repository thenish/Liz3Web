package de.liz3.liz3web.gui.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

/**
 * Created by yannh on 16.04.2017.
 */
public class DownloadFrameController {

    @FXML
    private ImageView imageView;
    @FXML
    private ProgressBar progessBar;
    @FXML
    private Label fileNameLabel;
    @FXML
    private JFXButton pauseBtn;
    @FXML
    private JFXButton cancelBtn;


    public ImageView getImageView() {
        return imageView;
    }

    public ProgressBar getProgessBar() {
        return progessBar;
    }

    public Label getFileNameLabel() {
        return fileNameLabel;
    }

    public JFXButton getPauseBtn() {
        return pauseBtn;
    }

    public JFXButton getCancelBtn() {
        return cancelBtn;
    }
}

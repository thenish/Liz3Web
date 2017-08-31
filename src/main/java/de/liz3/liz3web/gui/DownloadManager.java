package de.liz3.liz3web.gui;

import com.jfoenix.controls.JFXButton;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import de.liz3.liz3web.Liz3Web;
import de.liz3.liz3web.core.BrowserTab;
import de.liz3.liz3web.gui.controller.DownloadFrameController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by yannh on 16.04.2017.
 */
public class DownloadManager {

    double w = 0;
    private BrowserTab tab;
    private Stage stage;
    private BorderPane pane;
    private HBox hBox;


    public DownloadManager(BrowserTab tab) {
        this.tab = tab;
        pane = tab.getController().getPane();

    }


    private void setup() {

        Platform.runLater(() -> {
            BorderPane p = new BorderPane();
            hBox = new HBox(8);
            hBox.setPrefWidth(p.getWidth());
            p.setCenter(hBox);

            pane.setBottom(p);
            JFXButton button = new JFXButton("X");
            button.setOnAction(event -> pane.setBottom(null));
            p.setRight(button);
        });
    }

    public void handleFile(DownloadItem item) {
        if (hBox == null) setup();
        Platform.runLater(() -> {

            FXMLLoader loader = new FXMLLoader();
            BorderPane p = null;
            try {
                p = loader.load(Liz3Web.liz3Web.getClass().getResourceAsStream("/DownloadFrame.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            DownloadFrameController f = loader.getController();
            BorderPane finalP = p;
            f.getCancelBtn().setOnAction(event -> {
                item.cancel();
                f.getFileNameLabel().setText("Cancelled: " + f.getFileNameLabel().getText());
                //  hBox.getChildren().remove(finalP);
            });
            f.getPauseBtn().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (item.isPaused()) {
                        item.resume();
                        f.getPauseBtn().setText("Pause");
                        return;
                    }
                    item.pause();
                    f.getPauseBtn().setText("Resume");
                }
            });

            hBox.getChildren().add(p);
            f.getFileNameLabel().setText(item.getDestinationFile().getName());
            f.getProgessBar().setProgress(0);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    while (item.getPercentComplete() != 100) {
                        System.out.println(item.getPercentComplete());
                        updateProgress(item.getPercentComplete(), 100);
                        Thread.sleep(50);

                        if(item.isCanceled()) {
                           Platform.runLater(() -> {
                               f.getProgessBar().progressProperty().unbind();
                               f.getProgessBar().setProgress(100);
                               f.getProgessBar().setStyle("-fx-accent: red");
                             //  f.getFileNameLabel().setText("Finished: " + f.getFileNameLabel().getText());
                           });
                            return null;
                        }
                    }
                    return null;
                }
            };

            f.getProgessBar().progressProperty().bind(task.progressProperty());
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
            task.setOnSucceeded(event -> {
                        f.getProgessBar().progressProperty().unbind();
                        f.getProgessBar().setProgress(100);
                        f.getFileNameLabel().setText(f.getFileNameLabel().getText() + " - Finished");
                        f.getProgessBar().progressProperty().unbind();
                    }
            );

        });
    }
}

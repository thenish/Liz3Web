package de.liz3.liz3web.gui.controller;

import de.liz3.liz3web.browser.BrowserSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

/**
 * Created by yannh on 30.11.2016.
 */
public class PageSourceController {


    @FXML
    private Button cnlBtn;

    @FXML
    private TextArea editor;

    @FXML
    private Button onNewTab;

    @FXML
    private Button onActiveTab;

    @FXML
    private ListView<String> filesList;

    @FXML
    private Text stateText;


    public void initAsLoading() {

        stateText.setText("Parsing.....");
    }

    public void proceedAndShow(BrowserSource source) {



        filesList.getItems().add("HTML BODY");

        filesList.getItems().add("External CSS Files:");

        for (String str : source.getCssMap().keySet()) {


            filesList.getItems().add("External CSS: " + str);
        }
        filesList.getItems().add("External JavaScript Files:");
        for (String str : source.getJavaScriptMap().keySet()) {


            filesList.getItems().add("External JS: " + str);
        }

        filesList.setOnMouseReleased(event -> {

            String selected = filesList.getSelectionModel().getSelectedItem();

            if (selected.equalsIgnoreCase("HTML BODY")) {
                editor.setText(source.getHtmlSource().replace(";", ";\n\r").replace("}", "\n\r}").replace("{", "{\n\r\n" +
                        "\n"));
            }
            if (selected.startsWith("External CSS: ")) {

                for (String str : source.getCssMap().keySet()) {

                    if (selected.toLowerCase().contains(str.toLowerCase())) {
                        String s = source.getCssMap().get(str);
                        s = s.replace(";", ";\n\r").replace("}", "\n\r}").replace("{", "{\n\r\n" +
                                "\n\r");

                        editor.setText(s);
                    }
                }
            }
            if (selected.startsWith("External JS: ")) {

                for (String str : source.getJavaScriptMap().keySet()) {

                    if (selected.toLowerCase().contains(str.toLowerCase())) {
                        String s = source.getJavaScriptMap().get(str);
                        s = s.replace(";", ";\n\r").replace("}", "\n\r}").replace("{", "{\n\r\n" +
                                "\n");

                        editor.setText(s);

                    }
                }
            }
        });


        stateText.setText("Finished");
    }
}

package de.liz3.liz3web.browser;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import de.liz3.liz3web.gui.controller.SourceController;
import de.liz3.liz3web.util.HttpMethods;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by yannh on 22.02.2017.
 */
public class BrowserSource {

    private Browser browser;
    private SourceController controller;
    private Stage stage;
    private String url;

    public BrowserSource() {

    }

    public void show(Browser browser) {

        url = browser.getURL();
        this.browser = browser;

        if (stage == null) {

            stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/Debugger.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = loader.getController();

            stage.setScene(new Scene(root));
            stage.setTitle("Source: " + browser.getURL());
            stage.centerOnScreen();
            stage.sizeToScene();
        }
        controller.setup(browser.getDocument(), browser.getURL());
        controller.chromeDebugger(browser.getRemoteDebuggingURL());
        stage.show();
        getRaws();
    }

    private void getRaws() {


        DOMElement r = browser.getDocument().getDocumentElement();
        TreeItem<String> code = new TreeItem<>("Files");

        TreeItem<String> html = new TreeItem<>("Html");
        TreeItem<String> css = new TreeItem<>("Css");
        TreeItem<String> js = new TreeItem<>("JavaScript");


        for (DOMElement e : r.findElements(By.tagName("link"))) {

            if (e.getAttribute("rel").equalsIgnoreCase("stylesheet") || e.getAttribute("type").equalsIgnoreCase("text/css")) {
                String path = e.getAttribute("href");
                if(path.startsWith("//") || path.startsWith("http://") || path.startsWith("https://")) {

                    System.out.println("Parsing: " + path);
                    String hs = null;
                    try {
                        System.out.println("\tTrying: " + "https://" + path.split("//")[1]);
                        hs = HttpMethods.getSource("https://" + path.split("//")[1]);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    String h = null;
                    if(hs == null) {
                        try {
                            System.out.println("\tTrying: " + "http://" + path.split("//")[1]);
                            h = HttpMethods.getSource("http://" + path.split("//")[1]);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    TreeItem<String> it = new TreeItem<>(path);
                    if(hs != null) {

                        System.out.println(path + " https worked");
                        css.getChildren().add(it);
                        String finalHs = hs;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatCss(finalHs));
                        });
                    } else if( h != null) {

                        System.out.println(path + " http worked");

                        css.getChildren().add(it);
                        String finalH = h;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatCss(finalH));
                        });
                    } else {
                        return;
                    }

                } else {


                    String hs = null;
                    try {
                        System.out.println("\tTrying: " + "https://" + url.split("/")[2] + "/" +  path);
                        hs = HttpMethods.getSource("https://" + url.split("/")[2] + "/" +  path);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    String h = null;
                    if(hs == null) {
                        try {
                            System.out.println("\tTrying: " + "http://" + url.split("/")[2] + "/" +  path);
                            h = HttpMethods.getSource("http://" + url.split("/")[2] + "/" +  path);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    TreeItem<String> it = new TreeItem<>(path);
                    if(hs != null) {

                        System.out.println(path + " https worked");

                        css.getChildren().add(it);
                        String finalHs = hs;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatCss(finalHs));
                        });
                    } else if( h != null) {

                        System.out.println(path + " http worked");

                        css.getChildren().add(it);
                        String finalH = h;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatCss(finalH));
                        });
                    } else {
                        return;
                    }
                }

            }
        }

        for (DOMElement e : r.findElements(By.tagName("script"))) {

            if(!e.getAttribute("src").equalsIgnoreCase("")) {

                String path = e.getAttribute("src");
                System.out.println("Parsing: " + path);
                if(path.startsWith("//") || path.startsWith("http://") || path.startsWith("https://")) {

                    String hs = null;
                    try {
                        System.out.println("\tTrying: " + "https://"  +path.split("//")[1]);
                        hs = HttpMethods.getSource("https://" + path.split("//")[1]);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    String h = null;
                    if(hs == null) {
                        try {
                            System.out.println("\tTrying: " + "http://"  +path.split("//")[1]);

                            h = HttpMethods.getSource("http://" + path.split("//")[1]);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    TreeItem<String> it = new TreeItem<>(path);
                    if(hs != null) {

                        System.out.println(path + " https worked");

                        js.getChildren().add(it);
                        String finalHs = hs;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatJs(finalHs));
                        });
                    } else if( h != null) {
                        System.out.println(path + " http worked");

                        js.getChildren().add(it);
                        String finalH = h;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatJs(finalH));
                        });
                    } else {
                        return;
                    }

                } else {

                    String hs = null;
                    try {
                        System.out.println("\tTrying: " + "https://"+ url.split("/")[2]+ "/"  + path);

                        hs = HttpMethods.getSource("https://" + url.split("/")[2] + "/" +  path);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    String h = null;
                    if(hs == null) {
                        try {
                            System.out.println("\tTrying: " + "http://"+ url.split("/")[2] + "/" +  path);

                            h = HttpMethods.getSource("http://" + url.split("/")[2] + "/" +  path);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    TreeItem<String> it = new TreeItem<>(path);
                    if(hs != null) {

                        System.out.println(path + " https worked");

                        js.getChildren().add(it);
                        String finalHs = hs;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatJs(finalHs));
                        });
                    } else if( h != null) {
                        System.out.println(path + " http worked");

                        js.getChildren().add(it);
                        String finalH = h;
                        controller.actions.put(path, () -> {
                            controller.sourceArea.clear();
                            controller.sourceArea.setText(formatJs(finalH));
                        });
                    } else {
                        return;
                    }

                }

            }
        }

        code.getChildren().addAll(html, css, js);
        controller.getSourceTreeView().getRoot().getChildren().add(code);

        controller.actions.put("Html", () -> {

            controller.sourceArea.clear();

            controller.sourceArea.setText(r.getInnerHTML());
        });
    }

    private String formatJs(String text) {

        text = text.replace(";", ";\n").replace("{", "{\n").replace("}","\n}\n");

        return text;
    }
    private String formatCss(String text) {

        text = text.replace(";", ";\n").replace("{", "{\n").replace("}","\n}\n").replace(":", ": ");

        return text;
    }
}

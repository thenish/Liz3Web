package de.liz3.liz3web.browser;

import java.util.HashMap;

/**
 * Created by yannh on 30.11.2016.
 */
public class BrowserSource {

    private HashMap<String, String> scripts = new HashMap<>();
    private HashMap<String, String> css = new HashMap<>();
    private String htmlSource = "";


    public BrowserSource(String htmlSource) {

        this.htmlSource = htmlSource;
    }

    public void addScript(String name, String source) {

        scripts.put(name, source);
    }

    public void addCss(String name, String source) {

        css.put(name, source);
    }

    public HashMap<String, String> getCssMap() {
        return this.css;
    }
    public HashMap<String, String> getJavaScriptMap() {
        return this.scripts;
    }
    public String getHtmlSource() {
        return this.htmlSource;
    }
}

package de.liz3.liz3web.browserinc;

import de.liz3.liz3web.core.BrowserSettings;

/**
 * Created by yannh on 17.04.2017.
 */
public class SettingsImpl {

    private BrowserSettings bs;

    public SettingsImpl(BrowserSettings bs) {
        this.bs = bs;
    }

    public String println(String l) {
        return "Hat geklappt";
    }
}

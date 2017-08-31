package de.liz3.liz3web.browserinc;

/**
 * Created by yannh on 17.04.2017.
 */
public class BookMarkEntry {

    private long id;
    private String name;
    private String url;
    private String toolTip;
    private String IconPath;

    public BookMarkEntry(long id, String name, String url, String toolTip, String iconPath) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.toolTip = toolTip;
        IconPath = iconPath;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getToolTip() {
        return toolTip;
    }

    public String getIconPath() {
        return IconPath;
    }
}

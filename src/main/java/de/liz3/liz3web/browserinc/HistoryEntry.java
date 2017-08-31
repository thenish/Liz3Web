package de.liz3.liz3web.browserinc;


import java.util.Date;

/**
 * Created by yannh on 25.02.2017.
 */
public class HistoryEntry {

    private String url;
    private Date date;
    private long unixTime;
    private long id;

    public HistoryEntry(String url, Date date, long id) {
        this.url = url;
        this.date = date;
        unixTime = date.getTime() / 1000;
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public long getId() {
        return id;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setId(long id) {
        this.id = id;
    }
}

package com.songlee.htapp;

import java.io.Serializable;

/**
 * Created by walter_tong on 3/31/17.
 */

public class Bean implements Serializable {
    String text;
    String url;
    String itemUrl;

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
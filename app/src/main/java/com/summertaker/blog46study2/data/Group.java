package com.summertaker.blog46study2.data;

import java.io.Serializable;

public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private int logo;
    private String url;
    private String userAgent;

    public Group(String id, String name, int logo, String url, String userAgent) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.url = url;
        this.userAgent = userAgent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

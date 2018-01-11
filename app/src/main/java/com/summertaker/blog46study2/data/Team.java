package com.summertaker.blog46study2.data;

import java.io.Serializable;

public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String url;

    public Team() {

    }

    public Team(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

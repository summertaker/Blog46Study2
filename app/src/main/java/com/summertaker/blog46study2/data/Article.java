package com.summertaker.blog46study2.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Article implements Serializable, Comparable<Article> {

    private static final long serialVersionUID = 1L;

    private String title;
    private String name;
    private String date;
    private String time;
    private String html;
    private String text;
    private String url;
    private ArrayList<String> images = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
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

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @Override
    public int compareTo(@NonNull Article article) {
        return date.compareTo(article.date);
    }
}

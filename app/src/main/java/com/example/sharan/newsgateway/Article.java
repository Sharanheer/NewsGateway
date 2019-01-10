package com.example.sharan.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {

    String author;
    String title;
    String desc;
    String url;
    String urlToImage;
    String publishDate;

    public Article(){

    }

    public Article(String author, String title, String desc, String urlArticle, String urlImage, String publish) {
        this.author = author;
        this.title = title;
        this.desc = desc;
        this.url = urlArticle;
        this.urlToImage = urlImage;
        this.publishDate = publish;

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}

package com.markova.darya.audiocloud.model;

import com.google.firebase.database.Exclude;

public class ImageFile {
    //добавить еще расширение и дату загрузки
    private String title;
    private String url;
    private String contentType;
    private String generateTitle;
    private String key;

    private Long creationTime;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getGenerateTitle() {
        return generateTitle;
    }

    public void setGenerateTitle(String generateTitle) {
        this.generateTitle = generateTitle;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public ImageFile(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageFile() {}

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}

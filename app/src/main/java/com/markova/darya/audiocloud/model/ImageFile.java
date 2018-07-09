package com.markova.darya.audiocloud.model;

public class ImageFile {
    //добавить еще расширение и дату загрузки
    private String title;
    private String url;

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

}

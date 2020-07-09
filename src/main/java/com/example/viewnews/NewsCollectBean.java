package com.example.viewnews;

import org.litepal.crud.LitePalSupport;


public class NewsCollectBean extends LitePalSupport {

    private String userIdNumer;
    private String newsId;
    private String newSTitle;
    private String newsUrl;

    public String getUserIdNumer() {
        return userIdNumer;
    }

    public void setUserIdNumer(String userIdNumer) {
        this.userIdNumer = userIdNumer;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }


    public String getNewSTitle() {
        return newSTitle;
    }

    public void setNewSTitle(String newSTitle) {
        this.newSTitle = newSTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    @Override
    public String toString() {
        return "NewsCollectBean{" +
                "userIdNumer='" + userIdNumer + '\'' +
                ", newsId='" + newsId + '\'' +
                ", newSTitle='" + newSTitle + '\'' +
                ", newsUrl='" + newsUrl + '\'' +
                '}';
    }
}

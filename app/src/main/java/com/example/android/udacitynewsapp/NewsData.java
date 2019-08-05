package com.example.android.udacitynewsapp;


public class NewsData {

    private String authorName;
    private String authorURL;
    private String newsTitle;
    private String newsSectionName;
    private String newsPublicationDate;
    private String newsUrl;

    public NewsData(String authorName,
                    String authorURL,
                    String newsTitle,
                    String newsSectionName,
                    String newsPublicationDate,
                    String webURL) {

        this.authorName = authorName;
        this.authorURL = authorURL;
        this.newsTitle = newsTitle;
        this.newsSectionName = newsSectionName;
        this.newsPublicationDate = newsPublicationDate;
        this.newsUrl = webURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorURL() {
        return authorURL;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsSectionName() {
        return newsSectionName;
    }

    public String getNewsPublicationDate() {
        return newsPublicationDate;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

}

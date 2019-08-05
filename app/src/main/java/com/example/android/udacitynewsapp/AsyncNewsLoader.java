package com.example.android.udacitynewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class AsyncNewsLoader extends AsyncTaskLoader<List<NewsData>> {

    private String url;

    public AsyncNewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsData> loadInBackground() {
        if (url == null)
            return null;
        return QueryUtils.fetchNews(url);
    }
}

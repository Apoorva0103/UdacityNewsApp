package com.example.android.udacitynewsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    private static final String RESPONSE_HEADER = "response";
    private static final String RESPONSE_SUB_HEADER = "results";
    private static final String PARAM_TAGS = "tags";
    private static final String PARAM_WEB_TITLE = "webTitle";
    private static final String PARAM_WEB_URL = "webUrl";
    private static final String PARAM_SECTION_NAME = "sectionName";
    private static final String PARAM_WEB_PUBLICATION_DATE = "webPublicationDate";


    public static List<NewsData> fetchNews(String requestUrl) {
        URL url = formUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = httpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "HTTP error.", e);
        }

        return createNewsDataFromJsonResponse(jsonResponse);
    }

    private static URL formUrl(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL Malformed ", e);
        }
        return url;
    }

    private static String httpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                stream = urlConnection.getInputStream();
                jsonResponse = readFromStream(stream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching results.", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (stream != null)
                stream.close();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsData> createNewsDataFromJsonResponse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.length() == 0)
            return null;

        ArrayList<NewsData> newsList = new ArrayList<>();
        try {
            JSONObject responseJsonObj = new JSONObject(jsonResponse);
            JSONObject responseDataObj = responseJsonObj.getJSONObject(RESPONSE_HEADER);
            JSONArray resultsArray = responseDataObj.getJSONArray(RESPONSE_SUB_HEADER);

            if (resultsArray != null && resultsArray.length() > 0) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject newsInfo = resultsArray.getJSONObject(i);

                    String authorName = null;
                    String authorURL = null;

                    if (newsInfo.getJSONArray(PARAM_TAGS) != null
                            && newsInfo.getJSONArray(PARAM_TAGS).length() > 0) {
                        JSONObject authorInfo = newsInfo.getJSONArray(PARAM_TAGS).
                                getJSONObject(0);
                        authorName = authorInfo.getString(PARAM_WEB_TITLE);
                        authorURL = authorInfo.getString(PARAM_WEB_URL);
                    }


                    newsList.add(new NewsData(
                            authorName != null ? authorName : "Author Unknown",
                            authorURL,
                            newsInfo.getString(PARAM_WEB_TITLE),
                            newsInfo.getString(PARAM_SECTION_NAME),
                            newsInfo.getString(PARAM_WEB_PUBLICATION_DATE),
                            newsInfo.getString(PARAM_WEB_URL)));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing the JSON results", e);
        }

        return newsList;
    }
}

package com.example.newsapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.newsapp.MainActivity.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {

    private URL url;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    public NewsLoader(@NonNull Context context, String url) throws MalformedURLException {
        super(context);
        this.url = createUrl(url);
    }

    private URL createUrl(String urlString) throws MalformedURLException {
        return new URL(urlString);
    }

    @Nullable
    @Override
    public ArrayList<News> loadInBackground() {
        ArrayList<News> newsList = new ArrayList<>();
        String response = "";
        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            newsList = readDataFromJSON(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newsList;
    }

    private ArrayList<News> readDataFromJSON(String response) throws JSONException {
        ArrayList<News> newsList = new ArrayList<>();

        JSONObject baseObj = new JSONObject(response);
        baseObj = baseObj.getJSONObject("response");
        JSONArray results = baseObj.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject newsObj = results.getJSONObject(i);
            String title = newsObj.getString("webTitle");
            String section = newsObj.getString("sectionName");
            String webUrl = newsObj.getString("webUrl");
            String author = "";
            //verifying if there is an author
            if (newsObj.has("tags")) {
                JSONArray tags = newsObj.getJSONArray("tags");
                if (tags.length() > 0) author = tags.getJSONObject(0).getString("webTitle");
            }
            newsObj = newsObj.getJSONObject("blocks");
            String publishedAt = "";

            if (newsObj.has("main")) {
                JSONObject main = newsObj.getJSONObject("main");

                if (main.has("publishedDate")) publishedAt = main.getString("publishedDate");

            }

            String details = "";

            if (newsObj.has("body")) {
                JSONArray body = newsObj.getJSONArray("body");
                JSONObject bodyElem = body.getJSONObject(0);
                details = bodyElem.getString("bodyTextSummary");
            }

            newsList.add(new News(title, section, author, publishedAt, details, webUrl));

        }

        return newsList;
    }

    private String makeHttpRequest(URL url) throws IOException {
        HttpsURLConnection urlCon = (HttpsURLConnection) url.openConnection();
        urlCon.setRequestMethod("GET");
        urlCon.setReadTimeout(20000);
        urlCon.setConnectTimeout(20000);
        urlCon.connect();
        InputStream inputStream = urlCon.getInputStream();
        String response = readFromInputStream(inputStream);
        if (urlCon != null) {
            urlCon.disconnect();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return response;
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader buffer = new BufferedReader(inputStreamReader);
            String line = buffer.readLine();
            while (line != null) {
                response.append(line);
                line = buffer.readLine();
            }
        }
        return response.toString();
    }
}

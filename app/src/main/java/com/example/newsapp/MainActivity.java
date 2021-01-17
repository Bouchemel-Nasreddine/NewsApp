package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MainActivity.News>> {

    Uri.Builder builder = new Uri.Builder();
    RecyclerView newsRecycleView;
    NewsAdapter adapter;
    ArrayList<News> newsList = new ArrayList<>();
    MaterialButton settings, refresh;
    String order, page_size, section, from_date, to_date;
    TextView emptyStateView;
    ProgressBar loading;
    String problem = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPrefs();

        builder.scheme("https")
                .authority("content.guardianapis.com")
                .appendPath(section)
                .appendQueryParameter("from-date", from_date)
                .appendQueryParameter("to-date", to_date)
                .appendQueryParameter("show-blocks", "all")
                .appendQueryParameter("page-size", page_size)
                .appendQueryParameter("show-references", "author")
                .appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("api-key", "e3939392-e08e-4479-b4a1-3510b48300a8");

        LoaderManager manager = LoaderManager.getInstance(this);

        manager.initLoader(0, null, this);

        newsRecycleView = findViewById(R.id.newsRecycleView);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        settings = findViewById(R.id.preferences);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intent);
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsRecycleView.setVisibility(View.GONE);
                emptyStateView.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                manager.restartLoader(0, null, MainActivity.this);
            }
        });

        emptyStateView = findViewById(R.id.empty_view);

        loading = findViewById(R.id.loadingProgress);
    }

    @NonNull
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, @Nullable Bundle args) {
        if (!checkNetworkConnection()) {
            problem = getString(R.string.no_int);
        } else {
            problem = getString(R.string.no_news);
        }
        try {
            return new NewsLoader(this, builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<News>> loader, ArrayList<News> data) {
        newsList = data;
        adapter = new NewsAdapter(this, data);
        newsRecycleView.setAdapter(adapter);
        emptyStateView.setText(problem);
        setEmptyStateView(emptyStateView);
        loading.setVisibility(View.GONE);
        newsRecycleView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<News>> loader) {
        adapter.clear();
    }

    public static class News {
        private String title, section, author, datePublished, details, webUrl;

        public News(String title, String section, String author, String datePublished, String details, String webUrl) {
            this.title = title;
            this.section = section;
            this.author = author;
            this.datePublished = datePublished;
            this.details = details;
            this.webUrl = webUrl;
        }

        public String getTitle() {
            return this.title;
        }

        public String getSection() {
            return this.section;
        }

        public String getAuthor() {
            return author;
        }

        public String getDatePublished() {
            return datePublished;
        }

        public String getDetails() {
            return details;
        }

        public String getWebUrl() {
            return webUrl;
        }
    }

    private void loadPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences(PreferencesActivity.SHARED_PREFS, MODE_PRIVATE);

        order = sharedPrefs.getString("order", getString(R.string.relevance));
        page_size = sharedPrefs.getString("page_size", "100");
        section = sharedPrefs.getString("section", getString(R.string.all));
        from_date = sharedPrefs.getString("from_date", "2020-01-01");
        to_date = sharedPrefs.getString("to_date", "2020-01-12");
    }

    private void setEmptyStateView(TextView view) {
        if (newsList.isEmpty()) {
            view.setVisibility(View.VISIBLE);
            newsRecycleView.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.GONE);
            newsRecycleView.setVisibility(View.VISIBLE);
        }
    }

    // code(checkNetworkConnection) inspired from StackOverFlow by Chaouki Anass
    private boolean checkNetworkConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        return false;
    }

}

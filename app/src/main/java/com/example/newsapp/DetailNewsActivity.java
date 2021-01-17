package com.example.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.MainActivity.News;
import com.google.android.material.button.MaterialButton;

public class DetailNewsActivity extends AppCompatActivity {

    private News newsItem;
    MaterialButton back;
    TextView title, section, date, author, description, webUrl;

    public DetailNewsActivity() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);

        newsItem = NewsAdapter.clickedNews;

        back = findViewById(R.id.backBtn);
        title = findViewById(R.id.detailTitle);
        section = findViewById(R.id.detailSection);
        date = findViewById(R.id.detailDate);
        author = findViewById(R.id.detailAuthor);
        description = findViewById(R.id.detailDescription);
        webUrl = findViewById(R.id.hyperlink);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText(newsItem.getTitle());
        section.setText(newsItem.getSection());
        date.setText(newsItem.getDatePublished());
        if (newsItem.getAuthor() != null) author.setText(newsItem.getAuthor());
        if (newsItem.getDetails() != null) description.setText(newsItem.getDetails());

        webUrl.setMovementMethod(LinkMovementMethod.getInstance());
        webUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(newsItem.getWebUrl()));
                startActivity(intent);
            }
        });

    }
}

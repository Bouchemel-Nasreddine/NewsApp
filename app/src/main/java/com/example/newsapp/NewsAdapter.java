package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.MainActivity.News;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> newsList;
    private LayoutInflater inf;
    private Context context;
    public static News clickedNews;

    public NewsAdapter(Context context, ArrayList<News> list) {
        inf = LayoutInflater.from(context);
        this.newsList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inf.inflate(R.layout.news_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        News currentNews = newsList.get(position);
        holder.titleNews.setText(currentNews.getTitle());
        holder.sectionNews.setText(currentNews.getSection());
        holder.authorNews.setText(currentNews.getAuthor());
        holder.dateNews.setText(currentNews.getDatePublished());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedNews = newsList.get(position);
                Intent intent = new Intent(context, DetailNewsActivity.class);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleNews, sectionNews, authorNews, dateNews;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.titleNews = itemView.findViewById(R.id.newsTitle);
            this.sectionNews = itemView.findViewById(R.id.newsSection);
            this.authorNews = itemView.findViewById(R.id.newsAuthor);
            this.dateNews = itemView.findViewById(R.id.newsDate);
            this.layout = itemView.findViewById(R.id.newsItem);
        }
    }

    public void clear() {
        newsList.clear();
    }

}

package com.example.android.udacitynewsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<NewsData> newsList;

    public NewsRecyclerViewAdapter(Context cxt, List<NewsData> list) {
        context = cxt;
        newsList = list;
    }

    @Override
    public NewsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsData currentItem = newsList.get(position);
        holder.titleTextView.setText(currentItem.getNewsTitle());
        holder.authorTextView.setText(currentItem.getAuthorName());
        holder.dateTextView.setText(formatDate(currentItem.getNewsPublicationDate()));

        holder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newsURL = currentItem.getNewsUrl();
                if (newsURL != null && newsURL.length() > 0 &&
                        Patterns.WEB_URL.matcher(newsURL).matches()) {
                    context.startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(newsURL))
                    );
                } else {
                    Toast.makeText(context,
                            context.getString(R.string.newsLinkUnavailable),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.authorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authorURL = currentItem.getAuthorURL();
                if (authorURL != null && authorURL.length() > 0 &&
                        Patterns.WEB_URL.matcher(authorURL).matches()) {
                    context.startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(authorURL))
                    );
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null? newsList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView authorTextView;
        TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.list_item_title);
            authorTextView = itemView.findViewById(R.id.list_item_author_name);
            dateTextView = itemView.findViewById(R.id.list_item_date);
        }
    }

    private String formatDate(String str) {
        String[] parts = str.split("T");
        return parts[0];
    }

    public void addAll(List<NewsData> data) {
        this.newsList.clear();
        this.newsList.addAll(data);
        notifyDataSetChanged();
    }

}

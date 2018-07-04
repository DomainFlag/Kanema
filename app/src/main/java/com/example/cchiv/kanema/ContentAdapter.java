package com.example.cchiv.kanema;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private ArrayList<Movie> movies;

    public class ContentViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.movie_poster);
            textView = itemView.findViewById(R.id.movie_title);
        }
    }

    public ContentAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_layout, parent, false);

        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        Movie movie = movies.get(position);

        Glide
                .with(holder.imageView.getContext())
                .load(movie.getPosterPath())
                .into(holder.imageView);

        holder.textView.setText(movie.getTitle());
        holder.textView.setText(movie.getOverview());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}

package com.example.cchiv.kanema;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private ArrayList<Movie> movies;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private OnClickListener onListClickItem;

    public interface OnClickListener {
        void onListClickItem(int itemPosition);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageViewPoster;
        TextView textViewTitle;
        RatingBar ratingBarVote;
        TextView textViewReleaseDate;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            imageViewPoster = itemView.findViewById(R.id.movie_poster);
            textViewTitle = itemView.findViewById(R.id.movie_title);
            ratingBarVote = itemView.findViewById(R.id.movie_rating);
            ratingBarVote.setNumStars(5);

            textViewReleaseDate = itemView.findViewById(R.id.movie_date);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onListClickItem.onListClickItem(position);
        }
    }

    public ContentAdapter(ArrayList<Movie> movies, OnClickListener onListClickItem) {
        this.movies = movies;
        this.onListClickItem = onListClickItem;
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
                .with(holder.imageViewPoster.getContext())
                .load(movie.getPosterPath())
                .into(holder.imageViewPoster);

        holder.textViewTitle.setText(movie.getTitle());
        holder.ratingBarVote.setRating(movie.getVoteAverage()/2.0f);
        holder.textViewReleaseDate.setText(simpleDateFormat.format(movie.getReleaseDate()));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}

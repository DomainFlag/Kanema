package com.example.cchiv.kanema.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cchiv.kanema.Movie;
import com.example.cchiv.kanema.R;
import com.example.cchiv.kanema.data.MovieContract.MovieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;

    private Cursor cursor = null;
    private ArrayList<Movie> movies;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private OnClickListener onListClickItem;

    public ContentAdapter(Context context, ArrayList<Movie> movies, OnClickListener
            onListClickItem) {
        this.movies = movies;
        this.onListClickItem = onListClickItem;
    }

    public ContentAdapter(Context context, Cursor cursor, OnClickListener onListClickItem) {
        this.cursor = cursor;
        this.onListClickItem = onListClickItem;
    }

    @Override
    public void onItemDismiss(int position) {
        if(this.cursor != null) {
            int mEntryIndex = this.cursor.getColumnIndexOrThrow(MovieEntry._ID);

            this.context.getContentResolver().delete(Uri.parse("content://com.example.android" +
                    ".KanemaProvider"), MovieEntry._ID + "=" + String.valueOf(mEntryIndex), null);
        }
    }

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

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
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
        if(this.cursor != null) {
            this.cursor.moveToPosition(position);

            int posterPathIndexPos = this.cursor.getColumnIndex(MovieEntry.COL_MOVIE_POSTER_PATH);
            int titleIndexPox = this.cursor.getColumnIndex(MovieEntry.COL_MOVIE_TITLE);
            int voteAverageIndexPos = this.cursor.getColumnIndex(MovieEntry.COL_MOVIE_VOTE_AVERAGE);
            int releaseDateIndexPos = this.cursor.getColumnIndex(MovieEntry.COL_MOVIE_RELEASE_DATE);

            Glide
                    .with(holder.imageViewPoster.getContext())
                    .load(cursor.getString(posterPathIndexPos))
                    .into(holder.imageViewPoster);

            holder.textViewTitle.setText(this.cursor.getString(titleIndexPox));
            holder.ratingBarVote.setRating(this.cursor.getFloat(voteAverageIndexPos));
            holder.textViewReleaseDate.setText(this.cursor.getString(releaseDateIndexPos));
        } else {
            Movie movie = movies.get(position);

            Glide
                    .with(holder.imageViewPoster.getContext())
                    .load(movie.getPosterPath())
                    .into(holder.imageViewPoster);

            holder.textViewTitle.setText(movie.getTitle());
            holder.ratingBarVote.setRating(movie.getVoteAverage()/2.0f);
            holder.textViewReleaseDate.setText(simpleDateFormat.format(movie.getReleaseDate()));
        }
    }

    @Override
    public int getItemCount() {
        if(this.cursor != null)
            return this.cursor.getCount();

        return movies.size();
    }
}

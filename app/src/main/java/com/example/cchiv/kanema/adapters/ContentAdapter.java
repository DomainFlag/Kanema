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
import com.example.cchiv.kanema.objects.Content;
import com.example.cchiv.kanema.R;
import com.example.cchiv.kanema.data.ContentContract.ContentEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder>
        implements ItemTouchHelperAdapter {

    public interface OnClickListener {
        void onListClickItem(int itemPosition);
    }

    private Context context;

    private Cursor cursor = null;
    private ArrayList<Content> contents;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private OnClickListener onListClickItem;

    public ContentAdapter(Context context, ArrayList<Content> contents, OnClickListener
            onListClickItem) {
        this.context = context;
        this.contents = contents;
        this.onListClickItem = onListClickItem;
    }

    @Override
    public boolean onItemDismiss(int position) {
        if(cursor != null) {
            cursor.moveToPosition(position);

            int mEntryIndex = cursor.getColumnIndexOrThrow(ContentEntry._ID);

            int nbRowsDeleted = context.getContentResolver().delete(Uri.parse("content://com.example.android" +
                    ".KanemaProvider"), ContentEntry._ID + "=" + cursor.getString
                    (mEntryIndex), null);

            return nbRowsDeleted > 0;
        } else return false;
    }

    /* Replace cursor with new one at next fetch operation */
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public String getContentIdentifier(int itemPosition) {
        if(cursor != null) {
            cursor.moveToPosition(itemPosition);
            int identifierIndexPosition = cursor.getColumnIndexOrThrow(ContentEntry._ID);

            return cursor.getString(identifierIndexPosition);
        } else {
            return String.valueOf(contents.get(itemPosition).getID());
        }
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.content_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        if(cursor != null) {
            cursor.moveToPosition(position);

            int posterPathIndexPos = cursor.getColumnIndex(ContentEntry.COL_CONTENT_POSTER_PATH);
            int titleIndexPox = cursor.getColumnIndex(ContentEntry.COL_CONTENT_TITLE);
            int voteAverageIndexPos = cursor.getColumnIndex(ContentEntry.COL_CONTENT_VOTE_AVERAGE);
            int releaseDateIndexPos = cursor.getColumnIndex(ContentEntry.COL_CONTENT_RELEASE_DATE);

            Glide
                    .with(holder.imageViewPoster.getContext())
                    .load(cursor.getString(posterPathIndexPos))
                    .into(holder.imageViewPoster);

            holder.textViewTitle.setText(cursor.getString(titleIndexPox));
            holder.ratingBarVote.setRating(cursor.getFloat(voteAverageIndexPos));
            holder.textViewReleaseDate.setText(cursor.getString(releaseDateIndexPos));
        } else {
            Content content = contents.get(position);

            Glide
                    .with(holder.imageViewPoster.getContext())
                    .load(content.getPosterPath())
                    .into(holder.imageViewPoster);

            holder.textViewTitle.setText(content.getTitle());
            holder.ratingBarVote.setRating(content.getVoteAverage()/2.0f);
            holder.textViewReleaseDate.setText(simpleDateFormat.format(content.getReleaseDate()));
        }
    }

    @Override
    public int getItemCount() {
        if(cursor != null)
            return cursor.getCount();

        return contents.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageViewPoster;
        TextView textViewTitle;
        RatingBar ratingBarVote;
        TextView textViewReleaseDate;

        private ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            imageViewPoster = itemView.findViewById(R.id.movie_poster);
            textViewTitle = itemView.findViewById(R.id.movie_title);
            ratingBarVote = itemView.findViewById(R.id.movie_rating);
            textViewReleaseDate = itemView.findViewById(R.id.movie_date);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onListClickItem.onListClickItem(position);
        }
    }
}

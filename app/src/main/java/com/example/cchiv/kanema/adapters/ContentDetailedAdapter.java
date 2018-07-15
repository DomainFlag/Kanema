package com.example.cchiv.kanema.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cchiv.kanema.R;
import com.example.cchiv.kanema.data.ContentContract;
import com.example.cchiv.kanema.objects.Actor;
import com.example.cchiv.kanema.objects.Content;
import com.example.cchiv.kanema.objects.Review;
import com.example.cchiv.kanema.objects.Video;

import java.util.ArrayList;

public class ContentDetailedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewTypes {
        private static final int HEADER_VIEW = 0;
        private static final int VIDEO_VIEW = 1;
        private static final int REVIEW_VIEW = 2;
        private static final int ACTOR_VIEW = 3;
    }

    private Context context;
    private Content content;
    private ArrayList<Video> videos;
    private ArrayList<Review> reviews;
    private ArrayList<Actor> actors;
    private Cursor cursor = null;

    public ContentDetailedAdapter(Context context, Content content, ArrayList<Video> videos,
                                  ArrayList<Review> reviews, ArrayList<Actor> actors) {
        this.context = context;
        this.content = content;
        this.videos = videos;
        this.reviews = reviews;
        this.actors = actors;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        this.videos.clear();
        this.reviews.clear();
        this.actors.clear();
        this.content.setState(Content.LoadingStates.EMPTY);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater
                .from(this.context);

        View view;
        if(viewType == ViewTypes.HEADER_VIEW)
            return new ContentViewHolder(
                    layoutInflater
                            .inflate(R.layout.content_extra_layout, parent, false)
            );
        else if(viewType == ViewTypes.ACTOR_VIEW)
            return new ActorViewHolder(
                    layoutInflater
                            .inflate(R.layout.actor_layout, parent, false)
            );
        else if(viewType == ViewTypes.REVIEW_VIEW)
            return new ContentViewHolder(
                    layoutInflater
                            .inflate(R.layout.review_layout, parent, false)
            );
        else
            return new ContentViewHolder(
                    layoutInflater
                            .inflate(R.layout.video_layout, parent, false)
            );
    }

    private int getHeaderViews() {
        return (content.isLoaded() || cursor != null) ? 1 : 0;
    }

    private boolean checkIfActorView(int position) {
        return (actors.size() > 0 && (getHeaderViews() + videos.size() + reviews.size()) > position);
    }

    private boolean checkIfReviewView(int position) {
        int minBoundary = getHeaderViews() + videos.size();
        int maxBoundary = minBoundary + reviews.size();
        return (reviews.size() > 0 && (position > minBoundary && position < maxBoundary));
    }

    private boolean checkIfVideoView(int position) {
        int minBoundary = getHeaderViews();
        int maxBoundary = minBoundary + videos.size();
        return (videos.size() > 0 && (position > minBoundary && position < maxBoundary));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(position == 0 && (content.isLoaded() || cursor != null))
            onBindContentDetails((ContentViewHolder) holder);
        // Take into account the other View as it fills space too when loaded up
        else if(checkIfVideoView(position)) {
            onBindVideo((VideoViewHolder) holder, position - getHeaderViews());
        } else if(checkIfReviewView(position))
            onBindReview((ReviewViewHolder) holder, position - getHeaderViews() - videos.size());
        else if(checkIfActorView(position))
            onBindActor((ActorViewHolder) holder, position - getHeaderViews() - videos.size() - reviews.size());
    }

    private void onBindContentDetails(ContentViewHolder holder) {
        if(content.isLoaded()) {
            holder.contentDetailedTitle.setText(content.getTitle());

            if(!content.getTagline().isEmpty())
                holder.contentDetailedTagline.setText("( " + content.getTagline() + " )");
            else holder.contentDetailedTagline.setVisibility(View.GONE);

            holder.contentDetailedOverview.setText(content.getOverview());
        } else if(cursor != null) {
            cursor.moveToFirst();

            int contentTitleIndex = cursor.getColumnIndexOrThrow(ContentContract.ContentEntry.COL_CONTENT_TITLE);
            int contentTaglineIndex = cursor.getColumnIndexOrThrow(ContentContract.ContentEntry.COL_CONTENT_TAGLINE);
            int contentOverviewIndex = cursor.getColumnIndexOrThrow(ContentContract.ContentEntry.COL_CONTENT_OVERVIEW);

            holder.contentDetailedTitle.setText(cursor.getString(contentTitleIndex));

            if(!cursor.getString(contentTaglineIndex).isEmpty())
                holder.contentDetailedTagline.setText("( " + cursor.getString(contentTaglineIndex) + " )");
            else holder.contentDetailedTagline.setVisibility(View.GONE);

            holder.contentDetailedOverview.setText(cursor.getString(contentOverviewIndex));
        }
    }

    private void onBindVideo(VideoViewHolder holder, int position) {
        if(cursor == null) {
            Video video = videos.get(position);

            holder.videoName.setText(video.getName());

            Glide
                    .with(holder.itemView.getContext())
                    .load(video.getKey())
                    .into(holder.videoThumbnail);
        }
    }

    private void onBindReview(ReviewViewHolder holder, int position) {
        if(cursor == null) {
            Review review = reviews.get(position);

            holder.reviewAuthor.setText(review.getAuthor());
            holder.reviewContent.setText(review.getContent());
        }
    }

    private void onBindActor(ActorViewHolder holder, int position) {
        if(cursor == null) {
            Actor actor = actors.get(position);

            holder.actorCharacter.setText(actor.getCharacter());
            holder.actorName.setText(actor.getName());

            Glide
                    .with(holder.itemView.getContext())
                    .load(actor.getProfilePath())
                    .into(holder.actorPortrait);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && (content.isLoaded() || cursor != null))
            return ViewTypes.HEADER_VIEW;
        else if(checkIfVideoView(position))
            return ViewTypes.VIDEO_VIEW;
        else if(checkIfReviewView(position))
            return ViewTypes.REVIEW_VIEW;
        else if(checkIfActorView(position))
            return ViewTypes.ACTOR_VIEW;
        else return -1;
    }

    @Override
    public int getItemCount() {
        if(cursor != null) {
            return cursor.getCount();
        } else {
            return actors.size() +
                    videos.size() +
                    reviews.size() +
                    content.getState();
        }
    }


    private class ContentViewHolder extends RecyclerView.ViewHolder {

        private TextView contentDetailedTitle;
        private TextView contentDetailedTagline;
        private TextView contentDetailedOverview;

        private ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            this.contentDetailedTitle = itemView.findViewById(R.id.content_detailed_title);
            this.contentDetailedTagline = itemView.findViewById(R.id.content_detailed_tagline);
            this.contentDetailedOverview = itemView.findViewById(R.id.content_detailed_overview);
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        private ImageView videoThumbnail;
        private TextView videoName;

        private VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            this.videoName = itemView.findViewById(R.id.video_name);
            this.videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthor;
        private TextView reviewContent;

        private ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            this.reviewAuthor = itemView.findViewById(R.id.review_author);
            this.reviewContent = itemView.findViewById(R.id.review_content);
        }
    }

    private class ActorViewHolder extends RecyclerView.ViewHolder {

        private ImageView actorPortrait;
        private TextView actorName;
        private TextView actorCharacter;

        private ActorViewHolder(@NonNull View itemView) {
            super(itemView);

            this.actorPortrait = itemView.findViewById(R.id.actor_profile);
            this.actorName = itemView.findViewById(R.id.actor_name);
            this.actorCharacter = itemView.findViewById(R.id.actor_character);
        }
    }
}

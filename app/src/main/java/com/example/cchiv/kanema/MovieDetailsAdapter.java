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

public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewTypes {
        private static final int ACTOR_VIEW = 0;
        private static final int HEADER_VIEW = 1;
    }

    private Movie movie;
    private ArrayList<Actor> actors;
    private OnClickActorListener onClickActorListener;

    private int nViewHeaders = 0;

    public interface OnClickActorListener {
        void OnClickListener(int actorPosition);
    }

    public MovieDetailsAdapter(ArrayList<Actor> actors, Movie movie, OnClickActorListener onClickActorListener, int nViewHeaders) {
        this.movie = movie;
        this.actors = actors;
        this.onClickActorListener = onClickActorListener;
        this.nViewHeaders = nViewHeaders;
    }

    public class MovieDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView movieDetailedTitle;
        private TextView movieDetailedTagline;
        private TextView movieDetailedOverview;


        public MovieDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            this.movieDetailedTitle = itemView.findViewById(R.id.movie_detailed_title);
            this.movieDetailedTagline = itemView.findViewById(R.id.movie_detailed_tagline);
            this.movieDetailedOverview = itemView.findViewById(R.id.movie_detailed_overview);
        }
    }

    public class ActorViewHolder extends RecyclerView.ViewHolder implements MovieDetailsAdapter.OnClickActorListener {

        private ImageView actorPortrait;
        private TextView actorName;
        private TextView actorCharacter;


        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);

            this.actorPortrait = itemView.findViewById(R.id.actor_profile);
            this.actorName = itemView.findViewById(R.id.actor_name);
            this.actorCharacter = itemView.findViewById(R.id.actor_character);
        }

        @Override
        public void OnClickListener(int actorPosition) {
            onClickActorListener.OnClickListener(actorPosition);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0)
            return new ActorViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.actor_layout, parent, false)
            );
        else
            return new MovieDetailsViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.movie_details_layout, parent, false)
            );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == 0 && this.movie.getInitialized() == 1)
            onBindMovieDetailsToViewHolder((MovieDetailsViewHolder) holder);
        else onBindActorToViewHolder((ActorViewHolder) holder, position);
    }

    public void onBindMovieDetailsToViewHolder(MovieDetailsViewHolder holder) {
        if(this.movie.getInitialized() != 0) {
            holder.movieDetailedTitle.setText(this.movie.getTitle());
            holder.movieDetailedTagline.setText(this.movie.getTagline());
            holder.movieDetailedOverview.setText(this.movie.getOverview());
        }
    }

    public void onBindActorToViewHolder(ActorViewHolder holder, int position) {
        Actor actor = actors.get(position-this.movie.getInitialized());

        holder.actorCharacter.setText(actor.getCharacter());
        holder.actorName.setText(actor.getName());

        Glide
                .with(holder.itemView.getContext())
                .load(actor.getProfilePath())
                .into(holder.actorPortrait);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && this.movie.getInitialized() == 1)
            return ViewTypes.HEADER_VIEW;
        else
            return ViewTypes.ACTOR_VIEW;
    }

    @Override
    public int getItemCount() {
        return this.actors.size()+this.movie.getInitialized();
    }
}

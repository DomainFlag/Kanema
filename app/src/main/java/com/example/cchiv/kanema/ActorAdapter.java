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

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {

    private ArrayList<Actor> actors;
    private OnClickActorListener onClickActorListener;

    public interface OnClickActorListener {
        void OnClickListener(int actorPosition);
    }

    public ActorAdapter(ArrayList<Actor> actors, OnClickActorListener onClickActorListener) {
        this.actors = actors;
        this.onClickActorListener = onClickActorListener;
    }

    public class ActorViewHolder extends RecyclerView.ViewHolder implements ActorAdapter.OnClickActorListener {

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
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActorViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.actor_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        Actor actor = actors.get(position);

        holder.actorCharacter.setText(actor.getCharacter());
        holder.actorName.setText(actor.getName());

        Glide
                .with(holder.itemView.getContext())
                .load(actor.getProfilePath())
                .into(holder.actorPortrait);
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }
}

package com.example.cchiv.kanema.objects;

import java.util.ArrayList;
import java.util.Date;

public class Content {

    private int state = LoadingStates.EMPTY;

    private int ID;
    private String title;
    private String posterPath;
    private String overview;
    private Float voteAverage;
    private Date releaseDate;
    private ArrayList<String> genres;
    private String tagline;

    public class LoadingStates {
        public static final int EMPTY = 0;
        public static final int LOADED = 1;
    }

    public Content() {}

    public Content(int ID, String title, String posterPath, String overview, Float voteAverage, Date releaseDate) {
        this.state = LoadingStates.LOADED;

        this.ID = ID;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public Content(int ID, String title, String posterPath, String overview, Float voteAverage, Date
            releaseDate, ArrayList<String> genres, String tagline) {
        this.state = LoadingStates.LOADED;

        this.ID = ID;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.tagline = tagline;
    }

    public void copy(Content content) {
        this.state = LoadingStates.LOADED;

        this.setID(content.getID());
        this.setOverview(content.getOverview());
        this.setPosterPath(content.getPosterPath());
        this.setReleaseDate(content.getReleaseDate());
        this.setTitle(content.getTitle());
        this.setVoteAverage(content.getVoteAverage());
        this.setTagline(content.getTagline());
        this.setGenres(content.getGenres());
    }

    public boolean isLoaded() {
        return this.state == LoadingStates.LOADED;
    }

    public int getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public String getTagline() {
        return tagline;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public int getID() {
        return ID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void setState(int state) {
        this.state = state;
    }
}

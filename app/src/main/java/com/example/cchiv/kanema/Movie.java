package com.example.cchiv.kanema;

import java.util.ArrayList;
import java.util.Date;

public class Movie {

    private int initialized = 0;

    private int ID;
    private String title;
    private String posterPath;
    private String overview;
    private Float voteAverage;
    private Date releaseDate;
    private ArrayList<String> genres;
    private String tagline;

    public Movie() {}

    public Movie(int ID, String title, String posterPath, String overview, Float voteAverage, Date releaseDate) {
        this.initialized = 1;

        this.ID = ID;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public Movie(String title, String posterPath, String overview, Float voteAverage, Date releaseDate, ArrayList<String> genres, String tagline) {
        this.initialized = 1;

        this.ID = ID;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.tagline = tagline;
    }

    public void copy(Movie movie) {
        this.initialized = 1;

        this.setID(movie.getID());
        this.setOverview(movie.getOverview());
        this.setPosterPath(movie.getPosterPath());
        this.setReleaseDate(movie.getReleaseDate());
        this.setTitle(movie.getTitle());
        this.setVoteAverage(movie.getVoteAverage());
        this.setTagline(movie.getTagline());
        this.setGenres(movie.getGenres());
    }

    public int getInitialized() {
        return initialized;
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

    public void setInitialized(int initialized) {
        this.initialized = initialized;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}

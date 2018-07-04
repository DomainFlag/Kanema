package com.example.cchiv.kanema;

public class Movie {

    private String title;
    private String posterPath;
    private String overview;

    public Movie(String title, String posterPath, String overview) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
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
}

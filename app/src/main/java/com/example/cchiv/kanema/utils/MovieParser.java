package com.example.cchiv.kanema.utils;

import com.example.cchiv.kanema.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieParser {

    public MovieParser() {}

    public ArrayList<Movie> parseMovieList(StringBuilder data) {
        String dataString = data.toString();
        ArrayList<Movie> movies = new ArrayList<>();

        if(dataString.isEmpty())
            return movies;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonArray = jsonObject.optJSONArray("results");

            for(int it = 0; it < jsonArray.length(); it++) {
                JSONObject jsonMovieObject = jsonArray.optJSONObject(it);
                String movieTitle = jsonMovieObject.optString("title");
                String moviePosterPath = "http://image.tmdb.org/t/p/w185/" + jsonMovieObject.optString("poster_path");
                String overview = jsonMovieObject.optString("overview");

                movies.add(new Movie(movieTitle, moviePosterPath, overview));
            }

            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }
}

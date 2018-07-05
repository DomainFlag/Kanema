package com.example.cchiv.kanema.utils;

import com.example.cchiv.kanema.Actor;
import com.example.cchiv.kanema.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResponseParser {

    private final static int ACTORS_MAX = 10;

    public ResponseParser() {}

    public ArrayList<Movie> parseMovieList(StringBuilder data) {
        String dataString = data.toString();
        ArrayList<Movie> movies = new ArrayList<>();

        if(dataString.isEmpty())
            return movies;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonArray = jsonObject.optJSONArray("results");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for(int it = 0; it < jsonArray.length(); it++) {
                JSONObject jsonMovieObject = jsonArray.optJSONObject(it);
                int movieID = jsonMovieObject.optInt("id");
                String movieTitle = jsonMovieObject.optString("title");
                String moviePosterPath = "http://image.tmdb.org/t/p/w185/" + jsonMovieObject.optString("poster_path");
                String overview = jsonMovieObject.optString("overview");
                Float voteAverage = (float) jsonMovieObject.optDouble("vote_average");

                Date releaseDate = simpleDateFormat.parse(jsonMovieObject.optString("release_date"));

                Movie movie = new Movie(movieID, movieTitle, moviePosterPath, overview, voteAverage, releaseDate);

                movies.add(movie);
            }

            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public ArrayList<Actor> parseActorList(StringBuilder data) {
        ArrayList<Actor> actors = new ArrayList<>();

        if(data == null)
            return actors;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return actors;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonArray = jsonObject.optJSONArray("cast");

            for(int it = 0; it < jsonArray.length() && it < ACTORS_MAX; it++) {
                JSONObject jsonActorObject = jsonArray.optJSONObject(it);

                int id = jsonActorObject.optInt("id");
                int gender = jsonActorObject.optInt("gender");
                String name = jsonActorObject.optString("name");
                String profilePath = "http://image.tmdb.org/t/p/w185/" + jsonActorObject.optString("profile_path");
                String character = jsonActorObject.optString("character");

                Actor actor = new Actor(id, character, gender, name, profilePath);

                actors.add(actor);
            }

            return actors;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return actors;
    }
}

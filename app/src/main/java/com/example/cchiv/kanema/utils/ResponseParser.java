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
import java.util.Locale;

public class ResponseParser {

    private final static int ACTORS_MAX = 10;

    public ResponseParser() {}

    public ArrayList<Movie> parseMovieList(StringBuilder data) {
        ArrayList<Movie> movies = new ArrayList<>();

        if(data == null)
            return movies;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return movies;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonArray = jsonObject.optJSONArray("results");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            for(int it = 0; it < jsonArray.length(); it++) {
                JSONObject jsonMovieObject = jsonArray.optJSONObject(it);
                int movieID = jsonMovieObject.optInt("id");

                String movieTitle = jsonMovieObject.optString("title");
                if(movieTitle.isEmpty())
                    movieTitle = jsonMovieObject.optString("name");

                String moviePosterPath = "http://image.tmdb.org/t/p/w185/" + jsonMovieObject.optString("poster_path");
                String overview = jsonMovieObject.optString("overview");
                Float voteAverage = (float) jsonMovieObject.optDouble("vote_average");

                String release_date = jsonMovieObject.optString("release_date");
                if(release_date.isEmpty())
                    release_date = jsonMovieObject.optString("first_air_date");

                Date releaseDate = simpleDateFormat.parse(release_date);

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

    public Movie parseMovieDetails(StringBuilder data) {
        if(data == null)
            return null;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return null;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            String posterPath = "http://image.tmdb.org/t/p/w185/" + jsonObject.optString("poster_path");

            JSONArray jsonGenresArray = jsonObject.optJSONArray("genres");
            ArrayList<String> genres = new ArrayList<>();
            for(int it = 0; it < jsonGenresArray.length(); it++) {
                genres.add(jsonGenresArray.optJSONObject(it).optString("name"));
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date releaseDate = simpleDateFormat.parse(jsonObject.optString("release_date"));
            String tagline = "(" + jsonObject.optString("tagline") + ")";

            String title = jsonObject.optString("original_title");
            String overview = jsonObject.optString("overview");
            Float voteAverage = Float.parseFloat(jsonObject.optString("vote_average")) / 2.0f;

            return new Movie(title, posterPath, overview, voteAverage, releaseDate, genres, tagline);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}

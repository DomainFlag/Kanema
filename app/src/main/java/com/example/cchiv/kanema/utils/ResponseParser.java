package com.example.cchiv.kanema.utils;

import com.example.cchiv.kanema.objects.Actor;
import com.example.cchiv.kanema.objects.Content;
import com.example.cchiv.kanema.objects.Review;
import com.example.cchiv.kanema.objects.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResponseParser {

    private final static int ACTORS_MAX = 5;
    private final static int VIDEOS_MAX = 3;
    private final static int REVIEWS_MAX = 5;

    public ResponseParser() {};

    public ArrayList<Content> parseContentList(StringBuilder data) {
        ArrayList<Content> contents = new ArrayList<>();

        if(data == null)
            return contents;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return contents;

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

                Content content = new Content(movieID, movieTitle, moviePosterPath, overview, voteAverage, releaseDate);

                contents.add(content);
            }

            return contents;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public Content parseContentDetails(StringBuilder data) {
        if(data == null)
            return null;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return null;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            int movieID = jsonObject.optInt("id");
            String posterPath = "http://image.tmdb.org/t/p/w185/" + jsonObject.optString("poster_path");

            JSONArray jsonGenresArray = jsonObject.optJSONArray("genres");
            ArrayList<String> genres = new ArrayList<>();
            for(int it = 0; it < jsonGenresArray.length(); it++) {
                genres.add(jsonGenresArray.optJSONObject(it).optString("name"));
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            String release_date = jsonObject.optString("release_date");
            if(release_date.isEmpty())
                release_date = jsonObject.optString("first_air_date");

            Date releaseDate = simpleDateFormat.parse(release_date);

            String tagline = jsonObject.optString("tagline");

            String title = jsonObject.optString("original_title");
            if(title.isEmpty())
                title = jsonObject.optString("name");

            String overview = jsonObject.optString("overview");
            Float voteAverage = Float.parseFloat(jsonObject.optString("vote_average")) / 2.0f;

            return new Content(movieID, title, posterPath, overview, voteAverage, releaseDate,
                    genres, tagline);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Video> parseVideoList(StringBuilder data) {
        ArrayList<Video> videos = new ArrayList<>();

        if(data == null)
            return videos;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return videos;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonVideoArray = jsonObject.optJSONArray("results");

            for(int it = 0; it < jsonVideoArray.length() && it < VIDEOS_MAX; it++) {
                JSONObject jsonVideoObject = jsonVideoArray.optJSONObject(it);

                String key = jsonVideoObject.optString("key");
                String name = jsonVideoObject.optString("name");
                String source = jsonVideoObject.optString("site");
                String type = jsonVideoObject.optString("type");

                videos.add(new Video(key, name, source, type));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videos;
    }

    public ArrayList<Review> parseReviewList(StringBuilder data) {
        ArrayList<Review> reviews = new ArrayList<>();

        if(data == null)
            return reviews;

        String dataString = data.toString();

        if(dataString.isEmpty())
            return reviews;

        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonReviewArray = jsonObject.optJSONArray("results");

            for(int it = 0; it < jsonReviewArray.length() && it < REVIEWS_MAX; it++) {
                JSONObject jsonReviewObject = jsonReviewArray.optJSONObject(it);

                String author = jsonReviewObject.optString("author");
                String content = jsonReviewObject.optString("content");

                reviews.add(new Review(author, content));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
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

                actors.add(new Actor(id, character, gender, name, profilePath));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return actors;
    }
}

package com.example.cchiv.kanema;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cchiv.kanema.utils.Constants;
import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.RecyclerViewMargin;
import com.example.cchiv.kanema.utils.ResponseParser;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailedActivity extends AppCompatActivity {

    private final static int FETCH_ACTORS = 0;
    private final static int FETCH_MOVIE_DETAILS = 1;

    private Movie movie = new Movie();
    private ArrayList<Actor> actors = new ArrayList<>();
    private MovieDetailsAdapter movieDetailsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detailed_layout);

        Intent intent = getIntent();

        RecyclerView recyclerView = findViewById(R.id.actor_list);
        recyclerView.setHasFixedSize(false);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_sibling));
        recyclerView.addItemDecoration(itemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        movieDetailsAdapter = new MovieDetailsAdapter(actors, movie, new MovieDetailsAdapter.OnClickActorListener() {
            @Override
            public void OnClickListener(int actorPosition) {

            }
        }, 1);
        recyclerView.setAdapter(movieDetailsAdapter);

        String movieIdentifier = String.valueOf(intent.getIntExtra("id", 0));

        fetchActors(movieIdentifier);
        fetchMovieDetails(movieIdentifier);
    }

    public void fetchActors(String movieIdentifier) {
        AsyncRequest asyncRequest = new AsyncRequest(this, actors, movieDetailsAdapter, FETCH_ACTORS);

        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.PATH_API)
                .appendPath(Constants.MOVIE)
                .appendPath(movieIdentifier)
                .appendPath(Constants.CREDITS)
                .appendQueryParameter(Constants.API_KEY, Constants.API_KEY_VALUE)
                .build();

        try {
            URL url = new URL(uri.toString());

            asyncRequest.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void fetchMovieDetails(String movieIdentifier) {
        AsyncRequest asyncRequest = new AsyncRequest(this, movie, movieDetailsAdapter, FETCH_MOVIE_DETAILS);

        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.PATH_API)
                .appendPath(Constants.MOVIE)
                .appendPath(movieIdentifier)
                .appendQueryParameter(Constants.API_KEY, Constants.API_KEY_VALUE)
                .appendQueryParameter(Constants.LANGUAGE, Constants.LANGUAGE_VALUE)
                .build();

        try {
            URL url = new URL(uri.toString());

            asyncRequest.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static class AsyncRequest extends AsyncTask<URL, Void, Void> {

        private WeakReference<Context> context;

        private HTTPFetchRequest httpFetchRequest;
        private ResponseParser responseParser;

        private final static int FETCH_ACTORS = 0;
        private final static int FETCH_MOVIE_DETAILS = 1;

        private int fetchType;

        private MovieDetailsAdapter movieDetailsAdapter;
        private ArrayList<Actor> actors;

        private Movie movie;

        private AsyncRequest(Context context, MovieDetailsAdapter movieDetailsAdapter, int fetchType) {
            this.context = new WeakReference<>(context);
            this.movieDetailsAdapter = movieDetailsAdapter;
            this.fetchType = fetchType;

            this.httpFetchRequest = new HTTPFetchRequest();
            this.responseParser = new ResponseParser();
        }

        private AsyncRequest(Context context, ArrayList<Actor> actors, MovieDetailsAdapter movieDetailsAdapter, int fetchType) {
            this(context, movieDetailsAdapter, fetchType);

            this.actors = actors;
        }

        private AsyncRequest(Context context, Movie movie, MovieDetailsAdapter movieDetailsAdapter, int fetchType) {
            this(context, movieDetailsAdapter, fetchType);

            this.movie = movie;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(URL... urls) {
            if(urls.length == 0)
                return null;

            if(this.fetchType != FETCH_ACTORS && this.fetchType != FETCH_MOVIE_DETAILS)
                return null;

            StringBuilder result = httpFetchRequest.fetchFromURL(urls[0]);

            if(this.fetchType == FETCH_ACTORS) {
                ArrayList<Actor> fetchedActors = responseParser.parseActorList(result);

                this.actors.clear();
                this.actors.addAll(fetchedActors);
            } else {
                this.movie.copy(responseParser.parseMovieDetails(result));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(this.fetchType != FETCH_ACTORS && this.fetchType != FETCH_MOVIE_DETAILS)
                return;

            if(this.fetchType == FETCH_MOVIE_DETAILS) {
                if(this.context != null) {
                    Activity activity = (Activity) this.context.get();

                    Glide.with(this.context.get())
                            .load(this.movie.getPosterPath())
                            .into((ImageView) activity.findViewById(R.id.movie_detailed_poster));

                    RatingBar ratingBar = activity.findViewById(R.id.movie_detailed_rating);
                    ratingBar.setNumStars(5);
                    ratingBar.setMax(5);
                    ratingBar.setRating(this.movie.getVoteAverage());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    TextView textView = activity.findViewById(R.id.movie_detailed_release_date);
                    textView.setText(simpleDateFormat.format(this.movie.getReleaseDate()));

                    TextView textView1 = activity.findViewById(R.id.movie_detailed_genres);
                    ArrayList<String> genres = this.movie.getGenres();
                    textView1.setText(TextUtils.join("\n", genres));
                }
            }

            this.movieDetailsAdapter.notifyDataSetChanged();
        }
    }
}

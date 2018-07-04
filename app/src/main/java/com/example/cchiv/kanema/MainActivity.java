package com.example.cchiv.kanema;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.MovieParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;
    private ContentAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        movies = new ArrayList<>();
        this.contentAdapter = new ContentAdapter(movies);
        recyclerView.setAdapter(contentAdapter);

        try {
            HTTPAsyncFetch httpAsyncFetch = new HTTPAsyncFetch(
                    new URL("http://api.themoviedb.org/3/movie/popular?page=2&api_key=df8d6e4f363b5e022083e3e093bb4ebc"),
                    contentAdapter);

            httpAsyncFetch.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class HTTPAsyncFetch extends AsyncTask<Void, Void, Void> {

        private HTTPFetchRequest httpFetchRequest;
        private URL url;
        private ContentAdapter contentAdapter;

        public HTTPAsyncFetch(URL url, ContentAdapter contentAdapter) {
            super();

            this.url = url;
            this.contentAdapter = contentAdapter;
            this.httpFetchRequest = new HTTPFetchRequest();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder stringBuilder = this.httpFetchRequest.fetchFromURL(this.url);

            MovieParser movieParser = new MovieParser();
            movies.clear();
            movies.addAll(movieParser.parseMovieList(stringBuilder));

            return null;
        }

        @Override
        protected void onPostExecute(Void none) {
            super.onPostExecute(none);

            this.contentAdapter.notifyDataSetChanged();
        }
    }
}

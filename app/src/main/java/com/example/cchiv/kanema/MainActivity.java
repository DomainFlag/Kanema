package com.example.cchiv.kanema;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.cchiv.kanema.utils.Constants;
import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.RecyclerViewMargin;
import com.example.cchiv.kanema.utils.ResponseParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class  MainActivity extends AppCompatActivity {

    private ContentAdapter contentAdapter;

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.movie_list);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_component));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        movies = new ArrayList<>();
        contentAdapter = new ContentAdapter(movies, new ContentAdapter.OnClickListener() {
            @Override
            public void onListClickItem(int itemPosition) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("id", movies.get(itemPosition).getID());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(contentAdapter);

        fetchMovies(Constants.FILTER_BY_RATING);
    }

    private boolean isThereNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_popularity) {
            fetchMovies(Constants.FILTER_BY_POPULARITY);
        } else if(item.getItemId() == R.id.menu_item_rating) {
            fetchMovies(Constants.FILTER_BY_RATING);
        }

        return super.onOptionsItemSelected(item);
    }

    public void fetchMovies(String filterBy) {
        if(!isThereNetwork())
            return;

        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.PATH_API)
                .appendPath(Constants.MOVIE)
                .appendPath(filterBy)
                .appendQueryParameter(Constants.API_KEY, Constants.API_KEY_VALUE)
                .appendQueryParameter(Constants.LANGUAGE, Constants.LANGUAGE_VALUE)
                .build();

        try {
            HTTPAsyncFetch httpAsyncFetch = new HTTPAsyncFetch(
                    new URL(uri.toString()),
                    this.contentAdapter);

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

            ResponseParser responseParser = new ResponseParser();
            movies.clear();
            movies.addAll(responseParser.parseMovieList(stringBuilder));

            return null;
        }

        @Override
        protected void onPostExecute(Void none) {
            super.onPostExecute(none);

            this.contentAdapter.notifyDataSetChanged();
        }
    }
}

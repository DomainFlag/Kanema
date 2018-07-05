package com.example.cchiv.kanema;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.ResponseParser;
import com.example.cchiv.kanema.utils.RecyclerViewMargin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_component));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        movies = new ArrayList<>();
        final ContentAdapter contentAdapter = new ContentAdapter(movies, new ContentAdapter.OnClickListener() {
            @Override
            public void onListClickItem(int itemPosition) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("id", movies.get(itemPosition).getID());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(contentAdapter);

        try {
            HTTPAsyncFetch httpAsyncFetch = new HTTPAsyncFetch(
                    new URL("http://api.themoviedb.org/3/movie/popular?page=2&api_key=YOUR&API&KEY"),
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

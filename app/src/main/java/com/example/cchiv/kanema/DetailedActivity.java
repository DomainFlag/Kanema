package com.example.cchiv.kanema;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.RecyclerViewMargin;
import com.example.cchiv.kanema.utils.ResponseParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    private ArrayList<Actor> actors;
    private ActorAdapter actorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detailed_layout);

        actors = new ArrayList<>();

        Intent intent = getIntent();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.actor_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_component));
        recyclerView.addItemDecoration(itemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        actorAdapter = new ActorAdapter(actors, new ActorAdapter.OnClickActorListener() {
            @Override
            public void OnClickListener(int actorPosition) {

            }
        });
        recyclerView.setAdapter(actorAdapter);

        AsyncRequest asyncRequest = new AsyncRequest(
                String.valueOf(intent.getIntExtra("id", 0))
        );
        asyncRequest.execute();
    }

    public class AsyncRequest extends AsyncTask<Void, Void, Void> {

        private HTTPFetchRequest httpFetchRequest;
        private ResponseParser responseParser;
        private URL url = null;

        public AsyncRequest(String resource) {
            httpFetchRequest = new HTTPFetchRequest();
            responseParser = new ResponseParser();

            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder
                    .scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(resource)
                    .appendPath("credits")
                    .appendQueryParameter("api_key", "YOUR&API&KEY")
                    .build();

            try {
                this.url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder result = httpFetchRequest.fetchFromURL(this.url);

            actors.clear();
            actors.addAll(responseParser.parseActorList(result));

            for(int it = 0; it < actors.size(); it++) {
                Log.v("oooo", actors.get(it).getName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            actorAdapter.notifyDataSetChanged();
        }
    }
}

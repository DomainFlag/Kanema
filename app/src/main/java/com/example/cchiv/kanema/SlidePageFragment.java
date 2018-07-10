package com.example.cchiv.kanema;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.kanema.utils.Constants;
import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.RecyclerViewMargin;
import com.example.cchiv.kanema.utils.ResponseParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SlidePageFragment extends Fragment {

    private final static int nbMovieCols = 2;

    private Context context;

    private ContentAdapter contentAdapter;
    private ArrayList<Movie> movies;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entertainment_main, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.movie_list);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_component));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.context, nbMovieCols, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        movies = new ArrayList<>();
        contentAdapter = new ContentAdapter(movies, new ContentAdapter.OnClickListener() {
            @Override
            public void onListClickItem(int itemPosition) {
                Intent intent = new Intent(container.getContext(), DetailedActivity.class);
                intent.putExtra("id", movies.get(itemPosition).getID());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(contentAdapter);

        fetchMovies(Constants.FILTER_BY_RATING);

        return view;
    }

    private boolean isThereNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void fetchMovies(String filterBy) {
        if(!isThereNetwork())
            return;

        Bundle bundle = this.getArguments();
        String value = bundle.getString(getString(R.string.entertainment_key));

        String search_by;
        if(value.equals(getString(R.string.entertainment_movies)))
            search_by = Constants.MOVIE;
        else search_by = Constants.TV;

        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.PATH_API)
                .appendPath(search_by)
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

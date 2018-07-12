package com.example.cchiv.kanema.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.kanema.DetailedActivity;
import com.example.cchiv.kanema.Movie;
import com.example.cchiv.kanema.R;
import com.example.cchiv.kanema.SettingsActivity;
import com.example.cchiv.kanema.adapters.ContentAdapter;
import com.example.cchiv.kanema.data.MovieContract.MovieEntry;
import com.example.cchiv.kanema.utils.Constants;
import com.example.cchiv.kanema.utils.HTTPFetchRequest;
import com.example.cchiv.kanema.utils.RecyclerViewMargin;
import com.example.cchiv.kanema.utils.ResponseParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SlidePageFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private final static int mNbComponentsCols = 2;
    private Context context;
    private Cursor cursor;

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.context, mNbComponentsCols, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        movies = new ArrayList<>();
        contentAdapter = new ContentAdapter(this.context, movies, new ContentAdapter.OnClickListener
                () {
            @Override
            public void onListClickItem(int itemPosition) {
                Intent intent = new Intent(container.getContext(), DetailedActivity.class);
                intent.putExtra("id", movies.get(itemPosition).getID());
                intent.putExtra("title", movies.get(itemPosition).getTitle());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(contentAdapter);
        fetchMovies(Constants.FILTER_BY_RATING);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_popularity) {
            fetchMovies(Constants.FILTER_BY_POPULARITY);
        } else if(item.getItemId() == R.id.menu_item_rating) {
            fetchMovies(Constants.FILTER_BY_RATING);
        } else if(item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this.context, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
        else if(value.equals(getString(R.string.entertainment_series)))
            search_by = Constants.TV;
        else {
            fetchStarred();

            return;
        }

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

    public void fetchStarred() {
        getLoaderManager().initLoader(1, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COL_MOVIE_TITLE,
                MovieEntry.COL_MOVIE_POSTER_PATH,
                MovieEntry.COL_MOVIE_OVERVIEW,
                MovieEntry.COL_MOVIE_VOTE_AVERAGE,
                MovieEntry.COL_MOVIE_RELEASE_DATE,
                MovieEntry.COL_MOVIE_GENRES,
                MovieEntry.COL_MOVIE_TAGLINE
        };

        return new CursorLoader(this.context, Uri.parse("content://com.example.android" +
                ".KanemaProvider"), projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contentAdapter.swapCursor(data);
        contentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        contentAdapter.swapCursor(null);
        contentAdapter.notifyDataSetChanged();
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

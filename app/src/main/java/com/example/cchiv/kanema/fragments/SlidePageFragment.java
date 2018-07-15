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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.kanema.objects.Content;
import com.example.cchiv.kanema.DetailedActivity;
import com.example.cchiv.kanema.R;
import com.example.cchiv.kanema.SettingsActivity;
import com.example.cchiv.kanema.adapters.ContentAdapter;
import com.example.cchiv.kanema.data.ContentContract.ContentEntry;
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
    private String typeContent;

    private ContentAdapter contentAdapter;
    private ArrayList<Content> contents = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entertainment_main, container, false);

        Bundle bundle = this.getArguments();
        if(bundle == null && getActivity() != null)
            getActivity().finish();

        typeContent = bundle.getString(getString(R.string.entertainment_key), getString(R.string.entertainment_movies_key));

        setHasOptionsMenu(true);

        /* Set up the RecyclerView */
        RecyclerView recyclerView = view.findViewById(R.id.movie_list);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_component));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.context,
                mNbComponentsCols, GridLayoutManager.VERTICAL, false);
        
        recyclerView.setLayoutManager(gridLayoutManager);


        setListGestures(recyclerView);
        setListAdapter(this.context, recyclerView, typeContent);

        setData(Constants.FILTER_BY_RATING);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_popularity) {
            setData(Constants.FILTER_BY_POPULARITY);
        } else if(item.getItemId() == R.id.menu_item_rating) {
            setData(Constants.FILTER_BY_RATING);
        } else if(item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this.context, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setListGestures(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {

                    @Override
                    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder
                            viewHolder) {
                        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

                        return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlags);
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();

                        boolean isItemDismissed = contentAdapter.onItemDismiss(position);
                        if(isItemDismissed)
                            contentAdapter.notifyItemRemoved(position);
                        else contentAdapter.notifyItemChanged(position);
                    }

                });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void setListAdapter(final Context context, RecyclerView recyclerView, final String typeContent) {
        contentAdapter = new ContentAdapter(context, contents, new ContentAdapter.OnClickListener() {
            @Override
            public void onListClickItem(int itemPosition) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("id", contentAdapter.getContentIdentifier(itemPosition));
                intent.putExtra("contentType", typeContent);

                startActivity(intent);
            }
        });

        recyclerView.setAdapter(contentAdapter);
    }

    private boolean isThereNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager == null)
            return false;

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public void setData(String filterBy) {
        if(typeContent.equals(getString(R.string.entertainment_movies)))
            fetchContent(Constants.MOVIE, filterBy);
        else if(typeContent.equals(getString(R.string.entertainment_series)))
            fetchContent(Constants.TV, filterBy);
        else if(typeContent.equals(getString(R.string.entertainment_starred)))
            fetchStarred();
    }

    public void fetchContent(String searchBy, String filterBy) {
        if(!isThereNetwork())
            return;

        Uri uri = new Uri.Builder()
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.PATH_API)
                .appendPath(searchBy)
                .appendPath(filterBy)
                .appendQueryParameter(Constants.API_KEY, Constants.API_KEY_VALUE)
                .appendQueryParameter(Constants.LANGUAGE, Constants.LANGUAGE_VALUE)
                .build();

        try {
            HTTPAsyncFetch httpAsyncFetch = new HTTPAsyncFetch(
                    new URL(uri.toString()),
                    this.contentAdapter,
                    this.contents);

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
                ContentEntry._ID,
                ContentEntry.COL_CONTENT_TITLE,
                ContentEntry.COL_CONTENT_POSTER_PATH,
                ContentEntry.COL_CONTENT_OVERVIEW,
                ContentEntry.COL_CONTENT_VOTE_AVERAGE,
                ContentEntry.COL_CONTENT_RELEASE_DATE,
                ContentEntry.COL_CONTENT_GENRES,
                ContentEntry.COL_CONTENT_TAGLINE
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

    private static class HTTPAsyncFetch extends AsyncTask<Void, Void, Void> {

        private URL url;
        private ContentAdapter contentAdapter;
        ArrayList<Content> contents;

        private HTTPAsyncFetch(URL url, ContentAdapter contentAdapter, ArrayList<Content> contents) {
            super();

            this.url = url;
            this.contentAdapter = contentAdapter;
            this.contents = contents;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder stringBuilder = new HTTPFetchRequest().fetchFromURL(this.url);
            ResponseParser responseParser = new ResponseParser();

            contents.clear();
            contents.addAll(responseParser.parseContentList(stringBuilder));

            return null;
        }

        @Override
        protected void onPostExecute(Void none) {
            super.onPostExecute(none);

            this.contentAdapter.notifyDataSetChanged();
        }
    }
}

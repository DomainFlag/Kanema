package com.example.cchiv.kanema;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cchiv.kanema.adapters.ContentDetailedAdapter;
import com.example.cchiv.kanema.data.ContentContract.ContentEntry;
import com.example.cchiv.kanema.objects.Actor;
import com.example.cchiv.kanema.objects.Content;
import com.example.cchiv.kanema.objects.Review;
import com.example.cchiv.kanema.objects.Video;
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

    private final Content content = new Content();
    private final ArrayList<Video> videos = new ArrayList<>();
    private final ArrayList<Review> reviews = new ArrayList<>();
    private final ArrayList<Actor> actors = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detailed_layout);


        /* Set ActionBar && Toolbar */
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* Set RecyclerView */
        RecyclerView recyclerView = findViewById(R.id.actor_list);

        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewMargin((int) getResources().getDimension(R.dimen.activity_margin_sibling));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ContentDetailedAdapter contentDetailedAdapter = new ContentDetailedAdapter(this, content, videos, reviews, actors);
        recyclerView.setAdapter(contentDetailedAdapter);


        /* Get Intent data && set up the UX */
        Intent intent = getIntent();

        String contentIdentifier = intent.getStringExtra("id");
        String contentType = intent.getStringExtra("contentType");

        initFetchOfData(contentDetailedAdapter, contentIdentifier, contentType);
        setCustomOptionsSelection(contentIdentifier);
    }

    public void setCustomOptionsSelection(final String contentIdentifier) {

        findViewById(R.id.home_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        findViewById(R.id.star_content)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageView imageView = (ImageView) view;

                        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.android.KanemaProvider"), null,
                                ContentEntry._ID + " = " + contentIdentifier, null, null);

                        if(cursor != null && cursor.getCount() > 0) {
                            int deletedRows = getContentResolver().delete(Uri.parse("content://com.example.android.KanemaProvider"),
                                    ContentEntry._ID + " = " + contentIdentifier, null);

                            if(deletedRows > 0)
                                imageView.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_star_faded));
                        } else {
                            if(content.isLoaded()) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.US);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ContentEntry._ID, content.getID());
                                contentValues.put(ContentEntry.COL_CONTENT_TITLE, content.getTitle());
                                contentValues.put(ContentEntry.COL_CONTENT_GENRES,
                                        TextUtils.join(" ", content.getGenres()));
                                contentValues.put(ContentEntry.COL_CONTENT_OVERVIEW, content.getOverview());
                                contentValues.put(ContentEntry.COL_CONTENT_POSTER_PATH, content.getPosterPath());
                                contentValues.put(ContentEntry.COL_CONTENT_RELEASE_DATE,
                                        simpleDateFormat.format(content.getReleaseDate()));
                                contentValues.put(ContentEntry.COL_CONTENT_TAGLINE, content.getTagline());
                                contentValues.put(ContentEntry.COL_CONTENT_VOTE_AVERAGE, content.getVoteAverage());

                                Uri uri = getContentResolver().insert(Uri.parse("content://com.example.android" +
                                        ".KanemaProvider"), contentValues);

                                if(ContentUris.parseId(uri) > 0)
                                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R
                                            .drawable.ic_star));
                            }
                        }

                        if(cursor != null)
                            cursor.close();
                    }
            });
    }

    public void initFetchOfData(ContentDetailedAdapter contentDetailedAdapter, String contentIdentifier, String contentType) {
        String contentTypePath;

        setStarComponent(contentIdentifier);

        if(contentType.equals(getString(R.string.entertainment_movies)))
            contentTypePath = Constants.MOVIE;
        else if(contentType.equals(getString(R.string.entertainment_series)))
            contentTypePath = Constants.TV;
        else {
            fetchStarredContent(contentDetailedAdapter, contentIdentifier);
            return;
        }

        fetchContent(contentDetailedAdapter, contentTypePath, contentIdentifier, null, AsyncRequest.FetchTypes.FETCH_CONTENT_DETAILS);
        fetchContent(contentDetailedAdapter, contentTypePath, contentIdentifier, Constants.VIDEOS, AsyncRequest.FetchTypes.FETCH_VIDEOS);
        fetchContent(contentDetailedAdapter, contentTypePath, contentIdentifier, Constants.REVIEWS, AsyncRequest.FetchTypes.FETCH_REVIEWS);
        fetchContent(contentDetailedAdapter, contentTypePath, contentIdentifier, Constants.CREDITS, AsyncRequest.FetchTypes.FETCH_ACTORS);
    }

    public void setStarComponent(String contentIdentifier) {
        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.android.KanemaProvider"), null,
                ContentEntry._ID + " = " + contentIdentifier, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            ImageView imageView = findViewById(R.id.star_content);
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star));

            cursor.close();
        }
    }

    public void fetchContent(ContentDetailedAdapter contentDetailedAdapter, String contentType,
                             String contentIdentifier, String contentFeature, int fetchType) {
        try {
            Uri.Builder builder =  new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.PATH_API)
                    .appendPath(contentType)
                    .appendPath(contentIdentifier);

            if(contentFeature != null)
                builder
                        .appendPath(contentFeature);

            Uri uri = builder
                    .appendQueryParameter(Constants.API_KEY, Constants.API_KEY_VALUE)
                    .build();

            AsyncRequest asyncRequest = new AsyncRequest(this, contentDetailedAdapter,
                    fetchType);
            asyncRequest.execute(new URL(uri.toString()));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void fetchStarredContent(ContentDetailedAdapter contentDetailedAdapter, String contentIdentifier) {

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

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.android.KanemaProvider"), projection,
                ContentEntry._ID + " = " + contentIdentifier, null, null);

        fillStarredContent(cursor);

        contentDetailedAdapter.swapCursor(cursor);
        contentDetailedAdapter.notifyDataSetChanged();
    }

    public void fillStarredContent(Cursor cursor) {
        if(!cursor.moveToFirst())
            return;

        int posterPathIndex = cursor.getColumnIndexOrThrow(ContentEntry.COL_CONTENT_POSTER_PATH);
        int voteAverageIndex = cursor.getColumnIndexOrThrow(ContentEntry.COL_CONTENT_VOTE_AVERAGE);
        int releaseDateIndex = cursor.getColumnIndexOrThrow(ContentEntry.COL_CONTENT_RELEASE_DATE);
        int genresIndex = cursor.getColumnIndexOrThrow(ContentEntry.COL_CONTENT_GENRES);

        Glide
                .with(this)
                .load(cursor.getString(posterPathIndex))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.empty_package)
                        .error(R.drawable.empty_package))
                .into((ImageView) findViewById(R.id.movie_detailed_poster));

        RatingBar ratingBar = findViewById(R.id.movie_detailed_rating);
        ratingBar.setRating(Float.valueOf(cursor.getString(voteAverageIndex)));

        TextView textView = findViewById(R.id.movie_detailed_release_date);
        textView.setText(cursor.getString(releaseDateIndex));

        textView = findViewById(R.id.movie_detailed_genres);
        textView.setText(TextUtils.join("\n\n", new String[] { cursor.getString(genresIndex) }));
    };

    public class AsyncRequest extends AsyncTask<URL, Void, Boolean> {

        private class FetchTypes {
            private final static int nbMaxTypes = 4;

            private final static int FETCH_CONTENT_DETAILS = 0;
            private final static int FETCH_VIDEOS = 1;
            private final static int FETCH_REVIEWS = 2;
            private final static int FETCH_ACTORS = 3;
        }

        private WeakReference<Context> context;
        private ContentDetailedAdapter contentDetailedAdapter;
        private int fetchType;

        private AsyncRequest(Context context, ContentDetailedAdapter contentDetailedAdapter, int
                fetchType) {
            this.context = new WeakReference<>(context);
            this.contentDetailedAdapter = contentDetailedAdapter;
            this.fetchType = fetchType;
        }

        @Override
        protected Boolean doInBackground(URL... urls) {

            /* Checking for url availability and valid fetchType operation */
            if(urls.length == 0 && (this.fetchType < 0 || this.fetchType > FetchTypes.nbMaxTypes))
                return null;


            StringBuilder result = new HTTPFetchRequest().fetchFromURL(urls[0]);
            ResponseParser responseParser = new ResponseParser();
            switch(this.fetchType) {
                case FetchTypes.FETCH_CONTENT_DETAILS : {
                    Content fetchedContent = responseParser.parseContentDetails(result);

                    if(fetchedContent != null) {
                        content.copy(fetchedContent);

                        return true;
                    }

                    break;
                }
                case FetchTypes.FETCH_VIDEOS : {
                    ArrayList<Video> fetchedContent = responseParser.parseVideoList(result);

                    if(fetchedContent != null && fetchedContent.size() > 0) {
                        videos.clear();
                        videos.addAll(fetchedContent);

                        return true;
                    }

                    break;
                }
                case FetchTypes.FETCH_REVIEWS : {
                    ArrayList<Review> fetchedContent = responseParser.parseReviewList(result);

                    if(fetchedContent != null && fetchedContent.size() > 0) {
                        reviews.clear();
                        reviews.addAll(fetchedContent);

                        return true;
                    }

                    break;
                }
                case FetchTypes.FETCH_ACTORS : {
                    ArrayList<Actor> fetchedContent = responseParser.parseActorList(result);

                    if(fetchedContent.size() != 0 && fetchedContent.size() > 0) {
                        actors.clear();
                        actors.addAll(fetchedContent);

                        return true;
                    }

                    break;
                }
                default : {
                    // throw new IllegalArgumentException("Unknown fetch operation type");
                    return false;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean contentWasUpdated) {
            super.onPostExecute(contentWasUpdated);

            Context context = this.context.get();

            /* Activity' context is lost */
            if(context == null)
                return;

            Activity activity = (Activity) context;

            /* In case the data wasn't updated no need to go further */
            if(!contentWasUpdated) {
                Glide
                        .with(context)
                        .load(R.drawable.empty_package)
                        .into((ImageView) activity.findViewById(R.id.movie_detailed_poster));
            } else {

                if(fetchType == FetchTypes.FETCH_CONTENT_DETAILS) {
                    String contentPosterPath = content.getPosterPath();

                    Glide
                            .with(context)
                            .load(contentPosterPath)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.empty_package)
                                    .error(R.drawable.empty_package))
                            .into((ImageView) activity.findViewById(R.id.movie_detailed_poster));

                    RatingBar ratingBar = activity.findViewById(R.id.movie_detailed_rating);
                    ratingBar.setRating(content.getVoteAverage());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    TextView textView = activity.findViewById(R.id.movie_detailed_release_date);
                    textView.setText(simpleDateFormat.format(content.getReleaseDate()));

                    TextView textView1 = activity.findViewById(R.id.movie_detailed_genres);
                    ArrayList<String> genres = content.getGenres();
                    textView1.setText(TextUtils.join("\n\n", genres));
                }

                /* No need to check other fetch types as the adapters they reside in are automatically notified & refreshed */
                contentDetailedAdapter.notifyDataSetChanged();
            }
        }
    }
}

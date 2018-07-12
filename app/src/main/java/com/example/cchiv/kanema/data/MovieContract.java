package com.example.cchiv.kanema.data;

import android.provider.BaseColumns;

public class MovieContract {

    MovieContract() {};

    public class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String _ID = "id";
        public static final String COL_MOVIE_TITLE = "title";
        public static final String COL_MOVIE_POSTER_PATH = "posterPath";
        public static final String COL_MOVIE_OVERVIEW = "overview";
        public static final String COL_MOVIE_VOTE_AVERAGE = "voteAverage";
        public static final String COL_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COL_MOVIE_GENRES = "genres";
        public static final String COL_MOVIE_TAGLINE = "tagline";

    }
}

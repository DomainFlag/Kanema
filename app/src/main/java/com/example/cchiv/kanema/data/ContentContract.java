package com.example.cchiv.kanema.data;

import android.provider.BaseColumns;

public class ContentContract {

    ContentContract() {};

    public class ContentEntry implements BaseColumns {

        public static final String TABLE_NAME = "content";

        public static final String _ID = "id";
        public static final String COL_CONTENT_TITLE = "title";
        public static final String COL_CONTENT_POSTER_PATH = "posterPath";
        public static final String COL_CONTENT_OVERVIEW = "overview";
        public static final String COL_CONTENT_VOTE_AVERAGE = "voteAverage";
        public static final String COL_CONTENT_RELEASE_DATE = "releaseDate";
        public static final String COL_CONTENT_GENRES = "genres";
        public static final String COL_CONTENT_TAGLINE = "tagline";

    }
}

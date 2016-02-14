package in.aviaryan.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.sql.SQLException;

import in.aviaryan.popularmovies.data.MovieContract.MovieEntry;

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int MOVIE_DETAIL = 2, MOVIE_LIST = 1;
    static String LOG_TAG = "Database";
    MovieDBHelper DBHelper;
    SQLiteDatabase database;

    public MovieProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case MOVIE_LIST: {
                long _id = database.insert(MovieEntry.TABLE_NAME, null, values);
                Log.v(LOG_TAG, "Inserted - id = " + _id);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(returnUri, null);
                    return returnUri;
                }
                return null;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public boolean onCreate() {
        DBHelper = new MovieDBHelper(getContext());
        database = DBHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case MOVIE_LIST: {
                retCursor = database.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_DETAIL: {
                retCursor = database.query(
                        MovieEntry.TABLE_NAME, projection, "_id = ?",
                        new String[]{uri.getLastPathSegment()}, null, null, sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not needed");
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = MovieContract.AUTHORITY;

        matcher.addURI(AUTHORITY, "/#", MOVIE_DETAIL);
        return matcher;
    }
}
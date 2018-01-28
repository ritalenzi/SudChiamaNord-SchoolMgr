package com.sudchiamanord.schoolmgr.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sudchiamanord.schoolmgr.provider.DBContract.KidEntry;
import com.sudchiamanord.schoolmgr.provider.DBContract.AdoptionEntry;

/**
 * Created by rita on 12/19/15.
 */
public class DBProvider extends ContentProvider
{
    private static final String TAG = DBProvider.class.getSimpleName();

    private DBHelper mOpenHelper;

    private static final int KID = 100;
    private static final int ADOPTION = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Helper method to match each URI to the VIDEO integers constant defined above.
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher()
    {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found. The code passed into the constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher (UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is created.
        matcher.addURI (DBContract.CONTENT_AUTHORITY, DBContract.PATH_KID, KID);
        matcher.addURI (DBContract.CONTENT_AUTHORITY, DBContract.PATH_ADOPTION, ADOPTION);
        return matcher;
    }

    private String getTable (Uri uri)
    {
        switch (sUriMatcher.match (uri)) {
            case KID:
                return KidEntry.KID_TABLE;

            case ADOPTION:
                return AdoptionEntry.ADOPTION_TABLE;

            default:
                throw new UnsupportedOperationException ("Unknown uri: " + uri);
        }
    }


    @Override
    public boolean onCreate()
    {
        mOpenHelper = new DBHelper (getContext());

        return true;
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder)
    {
        String table = getTable (uri);
        Cursor retCursor = mOpenHelper.getReadableDatabase().query (table, projection, selection,
                selectionArgs, null, null, sortOrder);
        retCursor.setNotificationUri (getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType (Uri uri)
    {
        return null;
    }

    @Override
    public Uri insert (Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        String table = getTable (uri);

        long id = db.insert (table, null, values);
        if (id > 0) {
            returnUri = KidEntry.buildUri (id); // TODO: add also AdoptionEntry.buildUri
        }
        else {
            Log.d (TAG, "Failed to insert row into " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange (uri, null);

        return returnUri;
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;
        String table = getTable (uri);
        rowsDeleted = db.delete (table, selection, selectionArgs);

        if ((selection == null) || (rowsDeleted != 0)) {
            getContext().getContentResolver().notifyChange (uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update  (Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        String table = getTable (uri);

        int rowsUpdated = db.update (table, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange (uri, null);
        }

        return rowsUpdated;
    }
}

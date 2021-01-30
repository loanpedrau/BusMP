package fr.istic.mob.busmp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.istic.mob.busmp.DatabaseHelper;
import fr.istic.mob.busmp.StartContract;

public class StarProvider extends ContentProvider {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase myDB;
    private static final String PROVIDER_NAME = "fr.istic.mob.busmp.provider.StarProvider";
    private static final UriMatcher uriMatcher;
    private static final int BUS_ROUTES_ALL = 1;
    private static final int TRIPS_ALL = 2;
    private static final int CALENDAR_ALL = 3;
    private static final int STOPS_ALL = 4;
    private static final int STOPS_TIMES_ALL = 4;

    // Alloue l'objet UriMatcher.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.BusRoutes.CONTENT_PATH, BUS_ROUTES_ALL);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.Trips.CONTENT_PATH, TRIPS_ALL);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.Calendar.CONTENT_PATH, CALENDAR_ALL);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.Stops.CONTENT_PATH, STOPS_ALL);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.StopTimes.CONTENT_PATH, STOPS_TIMES_ALL);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext().getApplicationContext());
        myDB = dbHelper.getWritableDatabase();
        return (myDB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {

        Cursor cursor = dbHelper.getReadableDatabase().query(
                uri.getPath().replace("/",""), projection, selection, selectionArgs, null, null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


}
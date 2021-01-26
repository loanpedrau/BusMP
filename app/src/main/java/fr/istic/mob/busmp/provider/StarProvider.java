package fr.istic.mob.busmp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import fr.istic.mob.busmp.DatabaseHelper;
import fr.istic.mob.busmp.SaveBusDatabase;
import fr.istic.mob.busmp.StartContract;

public class StarProvider extends ContentProvider {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase myDB;
    private static final String PROVIDER_NAME = "fr.istic.mob.busmp.provider.StarProvider";
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static final UriMatcher uriMatcher;
    private static final int ITEM = 1;
    private static final int COLLECTION = 2;

    // Alloue l'objet UriMatcher.
    // Une URI terminée par myPathToData correspondra à une requête sur une collection.
    // Une URI terminée par'/[rowID]' correspondra à un item.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.BusRoutes.CONTENT_PATH, COLLECTION);
        uriMatcher.addURI(PROVIDER_NAME, StartContract.BusRoutes.CONTENT_PATH+"/#", ITEM);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null,DATABASE_VERSION);
        myDB = dbHelper.getWritableDatabase();
        return (myDB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        Cursor cursor = SaveBusDatabase.getInstance(getContext()).routeDao().getAllRoutes();
        System.out.println("NB ROW : "+cursor.getCount());
        //Cursor retCursor = dbHelper.getReadableDatabase().query(
                //StartContract.BusRoutes.CONTENT_PATH, projection, selection, selectionArgs, null, null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case COLLECTION:
                return StartContract.BusRoutes.CONTENT_TYPE;
            case ITEM:
                return StartContract.BusRoutes.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("URI non supportée : " + uri);
        }
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
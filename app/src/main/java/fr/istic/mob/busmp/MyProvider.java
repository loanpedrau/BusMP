package fr.istic.mob.busmp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyProvider extends ContentProvider {

    public DatabaseHelper dbHelper;
    public SQLiteDatabase myDB;

    public static final int DATA_TABLE = 100;
    public static final int DATA_TABLE_DATE = 101;
    private static final String DATABASE_NAME = "myData.db";
    private static final int DATABASE_VERSION = 1;
    private static final UriMatcher uriMatcher;

    // Alloue l'objet UriMatcher.
    // Une URI terminée par myPathToData correspondra à une requête sur une collection.
    // Une URI terminée par'/[rowID]' correspondra à un item.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("MyAuthority", "myPathToData", COLLECTION);
        uriMatcher.addURI("MyAuthority ", " myPathToData /#", ITEM);
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

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("....");
        // S'il s'agit d'une requête sur une ligne, on limite le résultat.
        switch (uriMatcher.match(uri)) {
            case ITEM:
                qb.appendWhere(Constants.KEY_COL_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }

        // Si aucun ordre de tri n'est spécifié, tri par date/heure
        String orderBy;
        if (TextUtils.isEmpty(sort)) {
            orderBy = Constants.KEY_COL_NAME;
        } else {
            orderBy = sort;
        }

        // Applique la requête à la base.
        Cursor c = qb.query(myDB, projection, selection, selectionArgs, null, null, orderBy);

        // Enregistre le ContextResolver pour qu'il soit averti
        // si le résultat change.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Renvoie un curseur.
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case COLLECTION:
                return "vnd.android.cursor.dir/vnd.myCompany.contentType ";
            case ITEM:
                return "vnd.android.cursor.item/vnd.myCompany.contentType ";
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
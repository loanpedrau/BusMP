package fr.istic.mob.busmp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_BUS_ROUTE = "bus_route";
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_STOP = "stop";
    private static final String TABLE_STOP_TIME = "stop_time";
    private static final String TABLE_CALENDAR = "calendar";
    private static final String DATABASE_CREATE_1 =
            "CREATE TABLE IF NOT EXISTS bus_route " +
                    "(route_id integer ,agency_id integer ,route_short_name text ,route_long_name text ,route_desc text," +
                    "route_type integer , route_url text,route_color text,route_text_color text,route_sort_order integer)";

    private static final String DATABASE_CREATE_2 =
            "CREATE TABLE IF NOT EXISTS trip "+
                    "(route_id integer ,service_id integer ,trip_id integer ,trip_headsign text,trip_short_name text,direction_id integer," +
                    "block_id text,shape_id text,wheelchair_accessible integer,bikes_allowed text)";

    private static final String DATABASE_CREATE_3 =
            "CREATE TABLE IF NOT EXISTS stop "+
                    "(stop_id integer ,stop_code integer ,stop_name text ,stop_desc text ,stop_lat real,stop_lon real,zone_id integer ,stop_url text," +
                    "location_type text ,parent_station text,stop_timezone text,wheelchair_boarding integer)";

    private static final String DATABASE_CREATE_4 =
            "CREATE TABLE IF NOT EXISTS stop_time "+
                    "(trip_id integer,arrival_time text ,departure_time text ,stop_id integer ,stop_sequence text,stop_headsign text" +
                    ",pickup_type text ,drop_off_type text ,shape_dist_traveled text)";

    private static final String DATABASE_CREATE_5 =
            "CREATE TABLE IF NOT EXISTS calendar "+
                    "(service_id integer ,monday integer ,tuesday integer ,wednesday integer ,thursday integer ," +
                    "friday integer ,saturday integer ,sunday integer ,start_date text ,end_date text)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.openOrCreateDatabase(DATABASE_NAME,Context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_1);
        db.execSQL(DATABASE_CREATE_2);
        db.execSQL(DATABASE_CREATE_3);
        db.execSQL(DATABASE_CREATE_4);
        db.execSQL(DATABASE_CREATE_5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Upgrading database wich will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_BUS_ROUTE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CALENDAR);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STOP);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STOP_TIME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TRIP);
        onCreate(db);

    }
}

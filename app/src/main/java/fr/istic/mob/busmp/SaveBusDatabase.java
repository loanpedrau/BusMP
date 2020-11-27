package fr.istic.mob.busmp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Calendar.class, Route.class, Stop.class, Stop_time.class, Trip.class}, version = 1, exportSchema = false)
public abstract class SaveBusDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile SaveBusDatabase INSTANCE;

    // --- DAO ---
    public abstract CalendarDao calendarDao();
    public abstract RouteDao routeDao();
    public abstract StopDao stopDao();
    //public abstract StopTimeDao stopTimeDao();
    //public abstract TripDao tripDao();

    // --- INSTANCE ---
    public static SaveBusDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SaveBusDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SaveBusDatabase.class, "MyDatabase.db")
                            .addCallback(prepopulateDatabase())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ---

    private static Callback prepopulateDatabase(){
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

            }
        };
    }
}

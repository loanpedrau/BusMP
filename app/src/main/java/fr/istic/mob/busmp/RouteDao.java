package fr.istic.mob.busmp;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RouteDao {

    @Query("SELECT * FROM Route")
    Cursor getAllRoutes();

    @Query("SELECT * FROM Route WHERE route_id = :routeId")
    List<Route> getRoutes(int routeId);

    @Insert
    void insertRoute(Route route);

    @Update
    void updateRoute(Route route);

    @Query("DELETE FROM Route WHERE route_id = :routeId")
    int deleteRoute(long routeId);

}

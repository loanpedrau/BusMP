package fr.istic.mob.busmp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
@Dao
public interface TripDao {

    @Query("SELECT * FROM Trip WHERE trip_id = :trip_id ")
    LiveData<List<Stop_time>> getTrip(int trip_id);

    @Insert
    void insertTrip(Trip trip);

    @Update
    void updateTrip(Trip trip);

    @Query("DELETE FROM Trip WHERE trip_id = :trip_id ")
    int deleteTrip(int trip_id);
}

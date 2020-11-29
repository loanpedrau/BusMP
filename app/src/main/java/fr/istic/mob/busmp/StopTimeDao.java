package fr.istic.mob.busmp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StopTimeDao {

    @Query("SELECT * FROM Stop_time WHERE trip_id = :trip_id AND stop_id = :stop_id ")
    List<Stop_time> getStopTime(int trip_id, int stop_id );

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Stop_time> StopTime);

    @Update
    void updateStopTime(Stop_time StopTime);

    @Query("DELETE FROM Stop_time WHERE trip_id = :trip_id AND stop_id = :stop_id ")
    int deleteItem(int trip_id, int stop_id  );

}


package fr.istic.mob.busmp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StopDao {

    @Query("SELECT * FROM Stop")
    LiveData<List<Stop>> getStop();

    @Query("SELECT * FROM Stop WHERE stop_id = :stopId")
    LiveData<List<Stop>> getStop(int stopId);

    @Insert
    void insertStop(Stop stop);

    @Update
    void updateStop(Stop stop);

    @Query("DELETE FROM Stop WHERE stop_id = :stopId")
    int deleteStop(long stopId);

}

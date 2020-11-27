package fr.istic.mob.busmp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalendarDao {

    @Query("SELECT * FROM Calendar")
    LiveData<List<Calendar>> getAllCalendar();

    @Query("SELECT * FROM Calendar WHERE service_id = :serviceId")
    LiveData<List<Calendar>> getCalendar(int serviceId);

    @Insert
    void insertCalendar(Calendar calendar);

    @Update
    void updateCalendar(Calendar calendar);

    @Query("DELETE FROM Calendar WHERE service_id = :serviceId")
    int deleteCalendar(long serviceId);

}

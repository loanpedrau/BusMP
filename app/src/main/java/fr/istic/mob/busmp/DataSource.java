package fr.istic.mob.busmp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DataSource(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void restart(){
        dbHelper.onUpgrade(database,1,1);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
        database.close();
    }
}

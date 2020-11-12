package fr.istic.mob.busmp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class StarService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }





}

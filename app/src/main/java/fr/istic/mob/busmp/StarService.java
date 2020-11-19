package fr.istic.mob.busmp;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class StarService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    private final class ServiceHandler extends Handler {

        private static final String CHANNEL_ID = "0";

        public ServiceHandler(Looper looper){
            super(looper);
        }

        public void handlerMessage(Message msg){
            // Create an explicit intent for an Activity in your app
            //Intent intent = new Intent(this, AlertDetails.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            /**NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("My notification")
                    .setContentText("Hello World!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    //.setContentIntent(pendingIntent)
                    .setAutoCancel(true);**/

            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            //Notification notification = builder.build();
            // notificationId is a unique int for each notification that you must define
            //notificationManager.notify(notification., builder.build());
            System.out.println(msg.toString());
            stopSelf(msg.arg1);
        }
    }


}

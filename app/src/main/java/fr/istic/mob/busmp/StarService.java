package fr.istic.mob.busmp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StarService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper,this);
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

    private void requestNewFile() throws IOException {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL("https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-busmetro-horaires-gtfs-versions-td&q=");

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        System.out.println(result.toString());
    }

    private final class ServiceHandler extends Handler {

        private static final String CHANNEL_ID = "1";
        private Context context;

        public ServiceHandler(Looper looper, Context context){
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg){
            try {
                requestNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Create an explicit intent for an Activity in your app
            //Intent intent = new Intent(this, AlertDetails.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("title")
                    .setContentText("content")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, builder.build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ServiceHandler.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

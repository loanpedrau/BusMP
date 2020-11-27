package fr.istic.mob.busmp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StarService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Thread checkFileThread;
    private boolean checkFile = true;
    private String actualUrl = "";
    private List<String> fileNames = Arrays.asList("routes.txt", "trips.txt", "stops.txt","stop_times.txt","calendar.txt");
    private int nbFileUpload = 0;

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            Thread initBDD =  new Thread(new Runnable(){
                public void run() {
                    try {
                        System.out.println("Notification clicked");
                        downloadZipFile(intent.getStringExtra("url"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            initBDD.start();
        }
    };

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
        registerReceiver(myReceiver, new IntentFilter("Download"));
        Thread initAppWithFile =  new Thread(new Runnable(){
            public void run() {
                try {
                    setActualUrl(requestFile("https://data.explore.star.fr/api/records/1.0/search" +
                            "/?dataset=tco-busmetro-horaires-gtfs-versions-td&q=&sort=-publication").toString());
                    // l’appli télécharge automatiquement le premier fichier CSV/JSON à l’installation (le plus ancien donc : sort = -publication)
                    //pour pouvoir tester le service
                    System.out.println("FIRST FILE : "+getActualUrl());
                    downloadZipFile(getActualUrl());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        initAppWithFile.start();
        /**try {
            initAppWithFile.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }**/
        checkFileThread = new Thread(new Runnable(){
            public void run() {
                while(checkFile)
                {
                    try {
                        String url = requestFile("https://data.explore.star.fr/api/records/1.0/search" +
                                "/?dataset=tco-busmetro-horaires-gtfs-versions-td&q=&sort=publication").toString();
                        //recupere dernier fichier publié (sort = publication)
                        if(!url.equals(getActualUrl())) {
                            System.out.println("NEW FILE : "+url);
                            Message msg = mServiceHandler.obtainMessage();
                            msg.obj = url;
                            mServiceHandler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(60000); //warning : api limit 2000 call by day
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        checkFileThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        stopCheckFile();
        try {
            checkFileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void stopCheckFile(){
        this.checkFile = false;
    }

    public String getActualUrl(){
        return this.actualUrl;
    }

    public void setActualUrl(String actualUrl){
        this.actualUrl = actualUrl;
    }

    private StringBuilder requestFile(String urlSite) throws IOException {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            url = new URL(urlSite);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONObject firstRecord = (JSONObject) jsonObject.getJSONArray("records").get(0);
            JSONObject fields = (JSONObject) firstRecord.get("fields");
            stringBuilder.setLength(0);
            stringBuilder.append(fields.get("url").toString());
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return stringBuilder;
    }

    private synchronized void downloadZipFile(String url) throws IOException {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("update_progress_bar");
        broadcastIntent.putExtra("value",nbFileUpload);
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        InputStream in = connection.getInputStream();
        ZipInputStream zipIn = new ZipInputStream(in);
        ZipEntry entry = zipIn.getNextEntry();
        broadcastIntent.putExtra("size",entry.getSize());
        sendBroadcast(broadcastIntent);

        while(entry != null) {
            if (!entry.isDirectory() && fileNames.contains(entry.getName())) {
                File file = new File(getExternalFilesDir(null),entry.getName()); //external storage
                FileOutputStream  os = new FileOutputStream(file);
                streamCopy(zipIn, os);
                os.close();
                System.out.println("UNZIP : "+entry.getName());
                nbFileUpload++;
                broadcastIntent.putExtra("value",nbFileUpload);
                sendBroadcast(broadcastIntent);
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        System.out.println("FIN UNZIP");
        nbFileUpload =0;
        setActualUrl(url);
    }

    public static void streamCopy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[32 * 1024];
        int readCount;
        while ((readCount = in.read(buffer)) != -1) {
            out.write(buffer, 0, readCount);
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

    private final class ServiceHandler extends Handler {

        private static final String CHANNEL_ID = "1";
        private Context context;

        public ServiceHandler(Looper looper, Context context){
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg){
            // Create an explicit intent for a Service in your app
            Intent intent = new Intent("Download");
            intent.putExtra("url", (String) msg.obj);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("title")
                    .setContentText("content")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, builder.build());
        }
    }

}

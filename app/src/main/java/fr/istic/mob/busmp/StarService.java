package fr.istic.mob.busmp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StarService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Thread checkFileThread;
    private boolean checkFile = true;
    private String actualUrl = "";
    private List<String> fileNames = Arrays.asList("routes.txt", "trips.txt","stop_times.txt", "stops.txt","calendar.txt");
    private int nbFileUpload = 0;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private boolean downloadInProgress = true;

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final Thread initBDD =  new Thread(new Runnable(){
                public void run() {
                    try {
                        System.out.println("Notification clicked");
                        initDb();
                        downloadZipFile(intent.getStringExtra("url"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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
        try {
            initDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper,this);
    }

    public void initDb() throws Exception {
        dbHelper = new DatabaseHelper(this.getApplicationContext());
        db = dbHelper.getWritableDatabase();
    }

    public void closeDb() throws Exception {
        db.close();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
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

                } catch (IOException e ) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        initAppWithFile.start();
        checkFileThread = new Thread(new Runnable(){
            public void run() {
                while(checkFile)
                {
                    try {
                        String url = requestFile("https://data.explore.star.fr/api/records/1.0/search" +
                                "/?dataset=tco-busmetro-horaires-gtfs-versions-td&q=&sort=publication").toString();
                        //recupere dernier fichier publié (sort = publication)
                        if(!url.equals(getActualUrl()) && !downloadInProgress) {
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
        unregisterReceiver(myReceiver);
        stopCheckFile();
        try {
            checkFileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            closeDb();
        } catch (Exception e) {
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

    private void clearDatabase(){
        this.db.delete("bus_route",null,null);
        this.db.delete("trip",null,null);
        this.db.delete("stop",null,null);
        this.db.delete("calendar",null,null);
        this.db.delete("trip",null,null);
    }

    private synchronized void downloadZipFile(String url) throws Exception {
        downloadInProgress = true;
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
        clearDatabase();
        while(entry != null) {
            if (!entry.isDirectory() && fileNames.contains(entry.getName())) {
                File file = new File(getExternalFilesDir(null),entry.getName()); //external storage
                FileOutputStream  os = new FileOutputStream(file);
                streamCopy(zipIn, os);
                os.close();
                System.out.println("UNZIP : "+entry.getName());
                insertDataInDatabase(file);
                nbFileUpload++;
                broadcastIntent.putExtra("value",nbFileUpload);
                sendBroadcast(broadcastIntent);
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        nbFileUpload =0;
        setActualUrl(url);
        Intent broadcastIntentSpinner = new Intent();
        broadcastIntentSpinner.setAction("update_spinners");
        sendBroadcast(broadcastIntentSpinner);
        System.out.println("FIN INIT DATABASE");
        downloadInProgress = false;
        closeDb();
    }

    private void insertDataInDatabase(File file) {
        List<ContentValues> stops_time_values = new ArrayList<>();
        List<ContentValues> trips_values = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            // read the first line from the text file
            String line = br.readLine();
            line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                String[] attributes = line.split(",");
                switch(file.getName()){
                    case "routes.txt":
                        Route route = new Route(attributes);
                        ContentValues routeValues = getRouteContentValues(route);
                        db.insert("bus_route", null, routeValues);
                        break;
                    case "trips.txt":
                        Trip trip = new Trip(attributes);
                        ContentValues tripValues = getTripContentValues(trip);
                        trips_values.add(tripValues);
                        break;
                    case "stops.txt" :
                        Stop stop = new Stop(attributes);
                        ContentValues stopValues = getStopContentValues(stop);
                        db.insert("stop", null, stopValues);
                        break;
                    case "stop_times.txt" :
                        Stop_time stop_time = new Stop_time(attributes);
                        ContentValues stopTimeValues = getStopTimeContentValues(stop_time);
                        stops_time_values.add(stopTimeValues);
                        break;
                    case "calendar.txt" :
                        Calendar calendar = new Calendar(attributes);
                        ContentValues calendarValues = getCalendarContentValues(calendar);
                        db.insert("calendar", null, calendarValues);
                        break;
                }
                line = br.readLine();
            }
        } catch (IOException ioe) { ioe.printStackTrace(); }
        if(file.getName().equals("stop_times.txt")) {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                for (ContentValues val : stops_time_values) {
                    db.insert("stop_time", null, val);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }else if(file.getName().equals("trips.txt")){
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                for (ContentValues val : trips_values) {
                    db.insert("trip", null, val);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    private ContentValues getRouteContentValues(Route route){
        ContentValues values = new ContentValues();
        values.put("route_id",route.getRoute_id());
        values.put("agency_id", route.getAgency_id());
        values.put("route_short_name", route.getRoute_short_name());
        values.put("route_long_name",route.getRoute_long_name());
        values.put("route_desc", route.getRoute_desc());
        values.put("route_type", route.getRoute_type());
        values.put("route_url", route.getRoute_url());
        values.put("route_color",route.getRoute_color());
        values.put("route_text_color", route.getRoute_text_color());
        values.put("route_sort_order", route.getRoute_sort_order());
        return values;
    }

    private ContentValues getTripContentValues(Trip trip){
        ContentValues values = new ContentValues();
        values.put("route_id",trip.getRoute_id());
        values.put("service_id", trip.getService_id());
        values.put("trip_id", trip.getTrip_id());
        values.put("trip_headsign",trip.getTrip_headsign());
        values.put("trip_short_name", trip.getTrip_short_name());
        values.put("direction_id", trip.getDirection_id());
        values.put("block_id", trip.getBlock_id());
        values.put("shape_id",trip.getShape_id());
        values.put("wheelchair_accessible", trip.getWheelchair_accessible());
        values.put("bikes_allowed", trip.getBikes_allowed());
        return values;
    }

    private ContentValues getStopContentValues(Stop stop){
        ContentValues values = new ContentValues();
        values.put("stop_id",stop.getStop_id());
        values.put("stop_code", stop.getStop_code());
        values.put("stop_name", stop.getStop_name());
        values.put("stop_desc",stop.getStop_desc());
        values.put("stop_lat", stop.getStop_lat());
        values.put("stop_lon", stop.getStop_lon());
        values.put("zone_id", stop.getZone_id());
        values.put("stop_url",stop.getStop_url());
        values.put("location_type", stop.getLocation_type());
        values.put("parent_station", stop.getParent_station());
        values.put("stop_timezone", stop.getStop_timezone());
        values.put("wheelchair_boarding", stop.getWheelchair_boarding());
        return values;
    }

    private ContentValues getCalendarContentValues(Calendar calendar){
        ContentValues values = new ContentValues();
        values.put("service_id",calendar.getService_id());
        values.put("monday", calendar.getMonday());
        values.put("tuesday", calendar.getTuesday());
        values.put("wednesday",calendar.getWednesday());
        values.put("thursday", calendar.getThursday());
        values.put("friday", calendar.getFriday());
        values.put("saturday", calendar.getSaturday());
        values.put("sunday",calendar.getSunday());
        values.put("start_date", calendar.getStart_date());
        values.put("end_date", calendar.getEnd_date());
        return values;
    }

    private ContentValues getStopTimeContentValues(Stop_time stopTime){
        ContentValues values = new ContentValues();
        values.put("trip_id",stopTime.getTrip_id());
        values.put("arrival_time", stopTime.getArrival_time());
        values.put("departure_time", stopTime.getDeparture_time());
        values.put("stop_id",stopTime.getStop_id());
        values.put("stop_sequence", stopTime.getStop_sequence());
        values.put("stop_headsign", stopTime.getStop_headsign());
        values.put("pickup_type", stopTime.getPickup_type());
        values.put("drop_off_type",stopTime.getDrop_off_type());
        values.put("shape_dist_traveled",stopTime.getShape_dist_traveled());
        return values;
    }

    public static void streamCopy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[32 * 1024];
        int readCount;
        while ((readCount = in.read(buffer)) != -1) {
            out.write(buffer, 0, readCount);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("url", (String) msg.obj);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("BusMP")
                    .setContentText("Une nouvelle mise à jour est disponible.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, builder.build());
        }
    }

}

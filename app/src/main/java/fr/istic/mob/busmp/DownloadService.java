package fr.istic.mob.busmp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadService extends Service {

    private int nbFileUpload = 0;
    private List<String> fileNames = Arrays.asList("routes.txt", "trips.txt", "stops.txt","stop_times.txt","calendar.txt");

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("created");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Thread initBDD =  new Thread(new Runnable(){
            public void run() {
                try {
                    System.out.println("Notification clicked");
                    downloadZipFile(intent.getStringExtra("url"));

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        initBDD.start();
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void downloadZipFile(String url) throws IOException, InterruptedException {
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
                FileOutputStream os = new FileOutputStream(file);
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
    }

    public static void streamCopy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[32 * 1024];
        int readCount;
        while ((readCount = in.read(buffer)) != -1) {
            out.write(buffer, 0, readCount);
        }
    }
}

package fr.istic.mob.busmp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import fr.istic.mob.busmp.provider.StarProvider;

public class MainActivity extends AppCompatActivity {

    private MyBroadRequestReceiver receiver;
    private ProgressBar bar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            bar = (ProgressBar) findViewById(R.id.progressBar);
            progressText = findViewById(R.id.myTextProgress);
            bar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            IntentFilter filter = new IntentFilter("update_progress_bar");
            receiver = new MyBroadRequestReceiver();
            registerReceiver( receiver, filter);
            String urlForUpdate = getIntent().getStringExtra("url");
            Intent intent = new Intent(this, StarService.class);
            intent.putExtra("url", urlForUpdate);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter("update_progress_bar"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyBroadRequestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("update");
            bar.setVisibility(View.VISIBLE);
            int max_progress = intent.getIntExtra("max_progress",5);
            int value = intent.getIntExtra("value",1);
            System.out.println("max :"+max_progress+" value :"+value);
            if(bar.getMax() ==0){
                bar.setMax(max_progress);
            }
            bar.setProgress(value);
            if(value == max_progress){
                bar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                finish();
            }
        }
    }

}
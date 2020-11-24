package fr.istic.mob.busmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private MyBroadRequestReceiver receiver;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
        IntentFilter filter = new IntentFilter("update_progress_bar");
        receiver = new MyBroadRequestReceiver();
        registerReceiver( receiver, filter);
        Intent intent = new Intent(this, StarService.class);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    private class MyBroadRequestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("update");
            bar.setVisibility(View.VISIBLE);
            int max_progress = intent.getIntExtra("max_progress",5);
            int value = intent.getIntExtra("value",1);
            bar.setMax(max_progress);
            bar.setProgress(value);
            if(value == max_progress){
                bar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
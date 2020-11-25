package fr.istic.mob.busmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private MyBroadRequestReceiver receiver;
    private ProgressBar bar;
    private boolean progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            IntentFilter filter = new IntentFilter("update_progress_bar");
            receiver = new MyBroadRequestReceiver();
            registerReceiver( receiver, filter);
            Intent intent = new Intent(this, StarService.class);
            startService(intent);
        }else if(savedInstanceState != null && savedInstanceState.getBoolean("PROGRESS")){
            bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }else if(savedInstanceState != null && !savedInstanceState.getBoolean("PROGRESS")){
            bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putBoolean("PROGRESS", progress);
        super.onSaveInstanceState(outState);
    }

    private class MyBroadRequestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("update");
            bar = (ProgressBar) findViewById(R.id.progressBar);
            progress = true;
            int max_progress = intent.getIntExtra("max_progress",5);
            int value = intent.getIntExtra("value",1);
            System.out.println("max :"+max_progress+" value :"+value);
            bar.setMax(max_progress);
            bar.setProgress(value);
            if(value == max_progress){
                bar.setVisibility(View.GONE);
                progress = false;
            }
        }
    }
}
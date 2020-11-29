package fr.istic.mob.busmp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyBroadRequestReceiver receiver;
    private SpinnerReceiver spinnerReceiver;
    private ProgressBar bar;
    private Spinner spinnerLigne;
    private Spinner spinnerDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            bar = (ProgressBar) findViewById(R.id.progressBar);
            spinnerLigne = (Spinner) findViewById(R.id.spinner1);
            spinnerDirection = (Spinner) findViewById(R.id.spinner);
            bar.setVisibility(View.VISIBLE);
            IntentFilter filter = new IntentFilter("update_progress_bar");
            receiver = new MyBroadRequestReceiver();
            registerReceiver( receiver, filter);
            IntentFilter filterSpinner = new IntentFilter("update_spinners");
            spinnerReceiver = new SpinnerReceiver();
            registerReceiver(spinnerReceiver, filterSpinner);
            Intent intent = new Intent(this, StarService.class);
            startService(intent);
        }
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
            bar.setMax(max_progress);
            bar.setProgress(value);
            if(value == max_progress){
                bar.setVisibility(View.GONE);
            }
        }
    }
    private class SpinnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> lines = intent.getStringArrayListExtra("lines");
            System.out.println(lines);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,lines);
            spinnerLigne.setAdapter(dataAdapter);
            System.out.println("passage ici");
        }
    }

    /*
private void loadBusLineSpinner()
{
    // database

    // Spinner Drop down elements
    List<String> busLines = getAllBusLines();

    // Creating adapter for spinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, busLines);

    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // attaching data adapter to spinner
    spinner1.setAdapter(dataAdapter);
}

public List<String> getAllBusLines(){
    List<String> busLines = new ArrayList<String>();

    // Select All Query

    // looping through all rows and adding to list

    // closing connection

    // returning contatcs
    return busLines;
}*/
}
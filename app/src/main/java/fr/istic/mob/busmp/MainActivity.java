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
    private SpinnerReceiver spinnerReceiver;
    private ProgressBar bar;
    private Spinner spinnerLigne;
    private Spinner spinnerDirection;
    private ArrayList<String> dataLines;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            dataLines = new ArrayList<String>();
            bar = (ProgressBar) findViewById(R.id.progressBar);
            progressText = findViewById(R.id.myTextProgress);
            spinnerLigne = (Spinner) findViewById(R.id.spinner1);
            spinnerDirection = (Spinner) findViewById(R.id.spinner);
            bar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            IntentFilter filter = new IntentFilter("update_progress_bar");
            receiver = new MyBroadRequestReceiver();
            registerReceiver( receiver, filter);
            IntentFilter filterSpinner = new IntentFilter("update_spinners");
            spinnerReceiver = new SpinnerReceiver();
            registerReceiver(spinnerReceiver, filterSpinner);
            Intent intent = new Intent(this, StarService.class);

            startService(intent);
            spinnerLigne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> directions = new ArrayList<String>();
                    String line = dataLines.get(position);
                    String[] data = line.split(",");
                    directions.add(data[2]);
                    directions.add(data[3]);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,directions);
                    spinnerDirection.setAdapter(dataAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter("update_progress_bar"));
        registerReceiver(spinnerReceiver, new IntentFilter("update_spinners"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
            unregisterReceiver(spinnerReceiver);
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
                /**Intent intentQuit = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentQuit);**/
                finish();
            }
        }
    }
    private class SpinnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            dataLines = intent.getStringArrayListExtra("lines");
            ArrayList<String> titleLines = new ArrayList<String>();
            for(String line : dataLines){
                String[] data = line.split(",");
                titleLines.add(data[0]+","+data[1]);
            }
            CustomAdapter dataAdapter = new CustomAdapter(getApplicationContext(),titleLines);
            spinnerLigne.setAdapter(dataAdapter);
        }
    }
}
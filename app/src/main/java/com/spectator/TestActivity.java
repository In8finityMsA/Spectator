package com.spectator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    private TextView counter;
    private int count = 0;
    private TextView total;
    private TextView hourly;
    private ArrayList<Voter> voters = new ArrayList<>();
    private LinearLayout shortStatistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_counter);

        counter = (TextView) findViewById(R.id.counter);
        total = (TextView) findViewById(R.id.total_amount);
        hourly = (TextView) findViewById(R.id.hourly_amount);
        voters = new ArrayList<Voter>();
        shortStatistics = (LinearLayout) findViewById(R.id.short_statistics);


        counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Voter newVoter = new Voter(System.currentTimeMillis(), ++count);
                total.setText(String.format(Locale.GERMAN, "%d", count));
                hourly.setText(String.format(Locale.GERMAN, "%d", count));
            }
        });

        shortStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Details.class);

                startActivity(intent);

            }



        });


    }


    }

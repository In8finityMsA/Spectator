package com.spectator;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class Details extends AppCompatActivity {

    private ArrayList<Voter> voters;
    private int totally;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "null");
        }
        else {
            Log.e("extras", "not null");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), extras);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

}

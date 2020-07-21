package com.spectator;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Details extends AppCompatActivity {

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

        UniversalPagerAdapter universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[] {new HourAnalysisFragment(), new ListFragment()}, new String[] {"Graph", "List"}, extras);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(universalPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

}

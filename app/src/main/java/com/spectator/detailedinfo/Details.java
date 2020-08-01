package com.spectator.detailedinfo;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.spectator.BaseActivity;
import com.spectator.R;
import com.spectator.data.Day;
import com.spectator.utils.UniversalPagerAdapter;

public class Details extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();
        Day day = (Day) extras.getSerializable("day");

        UniversalPagerAdapter universalPagerAdapter = null;
        if (day.getMode() == Day.PRESENCE) {
            universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[]{new GraphsFragment(), new ListFragment()}, new String[]{getString(R.string.graphs), getString(R.string.list)}, extras);
        }
        else if (day.getMode() == Day.BANDS) {
            universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[]{new GraphsFragment(), new ListFragment()}, new String[] {getString(R.string.graphs), getString(R.string.list)}, extras);
        }
        else if (day.getMode() == Day.PRESENCE_BANDS) {

        }
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(universalPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

}

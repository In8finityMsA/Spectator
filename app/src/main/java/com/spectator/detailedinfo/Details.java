package com.spectator.detailedinfo;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.spectator.BaseActivity;
import com.spectator.R;
import com.spectator.data.Day;
import com.spectator.data.Voter;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.utils.UniversalPagerAdapter;

import java.util.ArrayList;

public class Details extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();
        Day day = (Day) extras.getSerializable("day");


        UniversalPagerAdapter universalPagerAdapter = null;
        if (day.getMode() == Day.PRESENCE) {
            if (extras.containsKey("voters"))
                universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[]{new GraphsFragment(), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder)extras.getBinder("voters")).getData())}, new String[]{getString(R.string.graphs), getString(R.string.list)}, extras);
            else Log.e("Details", "No voters key in extras, Mode: " + day.getMode());
        }
        else if (day.getMode() == Day.BANDS) {
            if (extras.containsKey("bands"))
                universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[]{new GraphsFragment(), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder)extras.getBinder("bands")).getData())}, new String[]{getString(R.string.graphs), getString(R.string.list)}, extras);
            else Log.e("Details", "No bands key in extras, Mode: " + day.getMode());
        }
        else if (day.getMode() == Day.PRESENCE_BANDS) {
            if (extras.containsKey("bands") && extras.containsKey("voters")) {
                universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[]{new GraphsFragment(), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder) extras.getBinder("voters")).getData()), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder) extras.getBinder("bands")).getData())}, new String[]{getString(R.string.graphs), getString(R.string.list), getString(R.string.list)}, extras);
            }
            else Log.e("Details", "No voters and bands key in extras, Mode: " + day.getMode());
        }

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(universalPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

}

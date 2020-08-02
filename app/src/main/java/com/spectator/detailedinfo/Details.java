package com.spectator.detailedinfo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

        TextView showComments = (TextView) findViewById(R.id.show_comments);
        showComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewComments.class);
                startActivity(intent);
            }
        });

        UniversalPagerAdapter universalPagerAdapter = null;
        if (day.getMode() == Day.PRESENCE) {
            if (extras.containsKey("voters") && extras.containsKey("totalVoters"))
                universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(),
                        new Fragment[]{new GraphsFragment(day.getName() + ".voters"), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder)extras.getBinder("voters")).getData(), extras.getInt("totalVoters"))},
                        new String[]{getString(R.string.graphs), getString(R.string.list)}, extras);
            else Log.e("Details", "No voters key in extras, Mode: " + day.getMode());
        }
        else if (day.getMode() == Day.BANDS) {
            if (extras.containsKey("bands") && extras.containsKey("totalBands"))
                universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(),
                        new Fragment[]{new GraphsFragment(day.getName() + ".bands"), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder)extras.getBinder("bands")).getData(), extras.getInt("totalBands"))},
                        new String[]{getString(R.string.graphs), getString(R.string.list)}, extras);
            else Log.e("Details", "No bands key in extras, Mode: " + day.getMode());
        }
        else if (day.getMode() == Day.PRESENCE_BANDS) {
            if (extras.containsKey("bands") && extras.containsKey("totalBands") && extras.containsKey("voters") && extras.containsKey("totalVoters")) {
                universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(),
                        new Fragment[]{new GraphsFragment(day.getName() + ".voters"), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder) extras.getBinder("voters")).getData(), extras.getInt("totalVoters")), new GraphsFragment(day.getName() + ".bands"), new ListFragment((ArrayList<Voter>) ((ObjectWrapperForBinder) extras.getBinder("bands")).getData(), extras.getInt("totalBands"))},
                        new String[]{getString(R.string.graphs) + "\n" + getString(R.string.voters), getString(R.string.list) + "\n" + getString(R.string.voters), getString(R.string.graphs) + "\n" + getString(R.string.bands), getString(R.string.list) + "\n" + getString(R.string.bands)}, extras);
            }
            else Log.e("Details", "No voters and bands key in extras, Mode: " + day.getMode());
        }

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(universalPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

}

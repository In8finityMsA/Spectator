package com.spectator;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.spectator.HourAnalysisFragment;
import com.spectator.ListFragment;
import com.spectator.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[] {"Graphs", "List"};
    private static final int length = TAB_TITLES.length;
    private final Fragment[] PAGER_FRAGMENTS;
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Bundle bundle) {
        super(fm);
        mContext = context;
        HourAnalysisFragment firstFragment = new HourAnalysisFragment();
        firstFragment.setArguments(bundle);
        ListFragment secondFragment = new ListFragment();
        secondFragment.setArguments(bundle);
        PAGER_FRAGMENTS = new Fragment[] {firstFragment, secondFragment};
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PAGER_FRAGMENTS[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return length;
    }
}
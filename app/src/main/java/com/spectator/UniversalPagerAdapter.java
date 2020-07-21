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

public class UniversalPagerAdapter extends FragmentPagerAdapter {

    private final String[] TAB_TITLES;
    private final int length;
    private final Fragment[] PAGER_FRAGMENTS;
    private final Context mContext;

    public UniversalPagerAdapter(Context context, FragmentManager fm, Fragment[] fragments, String[] pageTitles, @Nullable Bundle bundle) {
        super(fm);
        mContext = context;
        length = fragments.length;
        TAB_TITLES = pageTitles;
        if (bundle != null) {
            for(Fragment fragment:fragments) {
                fragment.setArguments(bundle);
            }
        }
        PAGER_FRAGMENTS = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return PAGER_FRAGMENTS[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return length;
    }
}
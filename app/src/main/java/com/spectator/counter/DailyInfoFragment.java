package com.spectator.counter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spectator.R;
import com.spectator.data.Day;


public class DailyInfoFragment extends Fragment {

    private static final String MY_SETTINGS = "Settings";
    private static final String IS_FIRST_TIME = "Is first time";
    private int mode;
    private TextView thisDay;
    private TextView thisDayLabel;
    private TextView thisDay2;
    private TextView thisDayLabel2;
    private VerticalViewPager viewPager;
    private boolean isFirstTime;
    private SharedPreferences sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_info_fragment, container, false);
        viewPager = (VerticalViewPager) ((MainCounterScreen)this.getActivity()).getPager();

        Bundle extras = getArguments();
        if (extras == null) {
            Log.e("DailyInfoExtras", "null");
        }
        else {
            Log.i("DailyInfoExtras", "not null");

            if (extras.containsKey("mode"))
                mode = extras.getInt("mode");
            else {
                Log.e("DailyInfoExtras", "No mode key");
            }
            String[] labels = extras.getStringArray("labels");

            if (mode == Day.BANDS || mode == Day.PRESENCE) {
                view = inflater.inflate(R.layout.daily_info_fragment, container, false);

                thisDay = view.findViewById(R.id.daily_amount);
                thisDayLabel = view.findViewById(R.id.daily_label);

                thisDayLabel.setText(labels[0]);
                thisDay.setText(String.valueOf(extras.getInt("daily")));
            } else if (mode == Day.PRESENCE_BANDS) {
                //Labels will be null
                view = inflater.inflate(R.layout.joint_daily_info_fragment, container, false);

                thisDay = view.findViewById(R.id.votes_counter_amount);
                thisDay2 = view.findViewById(R.id.ribbons_counter);

                thisDay.setText(String.valueOf(extras.getInt("daily")));
                thisDay2.setText(String.valueOf(extras.getInt("daily2")));
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = view.getContext().getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        isFirstTime = sp.getBoolean(IS_FIRST_TIME, true);
        if (isFirstTime) {
            toggleArrowsAnimation(view, true);
        }

        viewPager.addOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isFirstTime) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(IS_FIRST_TIME, false);
                    editor.apply();
                    toggleArrowsAnimation(view, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void toggleArrowsAnimation(View view, boolean isEnable) {
        ImageView imageViewLeft, imageViewRight, imageViewLeft1, imageViewRight1;
        imageViewLeft = (ImageView) view.findViewById(R.id.down_arrow_left);
        imageViewRight = (ImageView) view.findViewById(R.id.down_arrow_right);
        imageViewLeft1 = (ImageView) view.findViewById(R.id.down_arrow_left_1);
        imageViewRight1 = (ImageView) view.findViewById(R.id.down_arrow_right_1);

        if (isEnable) {
            //TODO: create animations array
            final Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);

            imageViewLeft.setVisibility(View.VISIBLE);
            imageViewRight.setVisibility(View.VISIBLE);
            imageViewLeft.startAnimation(animation);
            imageViewRight.startAnimation(animation);

            final Animation animation1 = new AlphaAnimation(0, 1);
            animation1.setStartOffset(500);
            animation1.setDuration(500);
            animation1.setInterpolator(new LinearInterpolator());
            animation1.setRepeatCount(Animation.INFINITE);
            animation1.setRepeatMode(Animation.RESTART);

            //animation1.setRepeatMode(Animation.REVERSE);
            imageViewLeft1.setVisibility(View.VISIBLE);
            imageViewRight1.setVisibility(View.VISIBLE);
            imageViewLeft1.startAnimation(animation1);
            imageViewRight1.startAnimation(animation1);
        }
        else {
            imageViewLeft.clearAnimation();
            imageViewRight.clearAnimation();
            imageViewLeft.setVisibility(View.GONE);
            imageViewRight.setVisibility(View.GONE);

            imageViewLeft1.clearAnimation();
            imageViewRight1.clearAnimation();
            imageViewLeft1.setVisibility(View.GONE);
            imageViewRight1.setVisibility(View.GONE);
        }
    }

    void setDaily(int daily, @Day.Position int position) {
        if (mode != Day.PRESENCE_BANDS) {
            if (thisDay != null)
                this.thisDay.setText(String.valueOf(daily));
        }
        else {
            if (thisDay != null && thisDay2 != null) {
                if (position == 0)
                    this.thisDay.setText(String.valueOf(daily));
                else if (position == 1)
                    this.thisDay2.setText(String.valueOf(daily));
            }
        }
    }

}


package com.spectator.counter;

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


public class DailyInfoFragment extends Fragment {

    private TextView thisDay;
    private ImageView imageViewLeft, imageViewRight, imageViewLeft1, imageViewRight1;
    private VerticalViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_info_fragment, container, false);
        thisDay = view.findViewById(R.id.daily_amount);
        viewPager = (VerticalViewPager) ((MainCounterScreen)this.getActivity()).getPager();

        Bundle extras = getArguments();
        if (extras == null) {
            Log.e("DailyInfoExtras", "null");
        }
        else {
            Log.i("DailyInfoExtras", "not null");
            thisDay.setText(String.valueOf(extras.getInt("daily")));
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: create animations array
        final Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        imageViewLeft = (ImageView) view.findViewById(R.id.down_arrow_left);
        imageViewRight = (ImageView) view.findViewById(R.id.down_arrow_right);
        imageViewLeft.startAnimation(animation);
        imageViewRight.startAnimation(animation);

        final Animation animation1 = new AlphaAnimation(0, 1);
        animation1.setStartOffset(500);
        animation1.setDuration(500);
        animation1.setInterpolator(new LinearInterpolator());
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(Animation.RESTART);
        //animation1.setRepeatMode(Animation.REVERSE);
        imageViewLeft1 = (ImageView) view.findViewById(R.id.down_arrow_left_1);
        imageViewRight1 = (ImageView) view.findViewById(R.id.down_arrow_right_1);
        imageViewLeft1.startAnimation(animation1);
        imageViewRight1.startAnimation(animation1);

        viewPager.addOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageViewLeft.clearAnimation();
                imageViewRight.clearAnimation();
                imageViewLeft.setVisibility(View.GONE);
                imageViewRight.setVisibility(View.GONE);
                imageViewLeft1.clearAnimation();
                imageViewRight1.clearAnimation();
                imageViewLeft1.setVisibility(View.GONE);
                imageViewRight1.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void setDaily(int daily) {
        if (thisDay != null)
            this.thisDay.setText(String.valueOf(daily));
    }

}


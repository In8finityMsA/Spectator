package com.spectator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class DailyFragment extends Fragment {

    private ImageView imageViewLeft, imageViewRight, imageViewLeft1, imageViewRight1;
    private VerticalViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_fragment, container, false);

        viewPager = (VerticalViewPager) ((MainCounterScreen)this.getActivity()).getPager();

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
}


package com.spectator.detailedinfo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spectator.R;
import com.spectator.data.Hour;
import com.spectator.utils.JsonIO;
import com.spectator.utils.ObjectWrapperForBinder;

import java.util.ArrayList;
import java.util.Arrays;

public class HourAnalysisFragment extends Fragment {

    private BarChart barChart;
    private String hourlyJsonPath;
    private JsonIO hourlyJsonIO;
    ArrayList<Hour> hours;
    ArrayList<BarEntry> voters;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hour_analysis_fragment, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();
        if (extras == null) {
            Log.e("extras", "null");
        }
        else {
            Log.i("extras", "not null");
            hourlyJsonPath = extras.getString("hourlyJsonPath");
        }

        hours = new ArrayList<>();
        voters = new ArrayList<>();

        hourlyJsonIO = new JsonIO(getContext().getFilesDir(), hourlyJsonPath, Hour.ARRAY_KEY, false);
        hours = hourlyJsonIO.parseJsonArray(true, hours, true, Hour.ARRAY_KEY, Hour.class, Hour.constructorArgs, Hour.jsonKeys);

        settingValues();

        barChart = (BarChart) view.findViewById(R.id.chart);
        //barChart.setFitBars(true);
        barChart.setMaxVisibleValueCount(24);
        barChart.setDrawValueAboveBar(true);
        barChart.setExtraOffsets(0, 0, 0, 10);
        Description desc = new Description();
        desc.setText(" ");
        barChart.setDescription(desc);
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        ValueFormatter xAxisFormatter = new DefaultAxisValueFormatter(2);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(24);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextSize(14);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setAxisMinimum(0f);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setAxisMinimum(0f);

        BarDataSet bardataset = new BarDataSet(voters, null);
        barChart.animateY(2000);
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.rgb("#0037EF"));
        barChart.setData(data);

    }

    private void settingValues2() {
        //Finding min and max value of Hour String [0;23]
        //Offset for xAxis to make bars between hour x and x+1, and not between x-1 and x
        int xOffset = 1;


        //Sorting hours list by string Hour unicode value
        for (int i = 0; i < hours.size(); i++) {
            for (int j = i + 1; j < hours.size(); j++) {
                if(hours.get(i).getHour().compareTo(hours.get(j).getHour()) > 0) {
                    Hour temp = hours.get(i);
                    hours.set(i, hours.get(j));
                    hours.set(j, temp);
                }
            }
        }

        int prevValue = Integer.MIN_VALUE;
        for (int i = 0; i < hours.size(); i++) {
            Hour hour = hours.get(i);
            //Filling gaps between hours
            int curValue = Integer.parseInt(hour.getHour());
            int difference = curValue - prevValue;
            if (difference > 1 && difference <= 23 && prevValue != Integer.MIN_VALUE) {
                while (difference > 1) {
                    difference--;
                    voters.add(new BarEntry(curValue - difference + xOffset, 0.15f));
                    //Log.i("GraphValue", i + " (difference : " + difference + "): " + (curValue - difference) + " prevValue: " + prevValue + " curValue: " + curValue);
                }
            }

            //Log.i("GraphValue", i + ": " + Integer.parseInt(hour.getHour()) + ", "  + hour.getCount());
            voters.add(new BarEntry(curValue + xOffset, hour.getCount()));
            prevValue = curValue;
        }
    }

    private void settingValues() {
        //Offset for xAxis to make bars between hour x and x+1, and not between x-1 and x
        int xOffset = 1;

        int min = 25; int max = -1; int curValue;
        for (int i = 0; i < hours.size(); i++) {
            curValue = Integer.parseInt(hours.get(i).getHour());
            if (curValue > max) {
                max = curValue;
            }
            if (curValue < min) {
                min = curValue;
            }
        }
        for (int i = min + 1; i < max; i++) {
            voters.add(new BarEntry(i + xOffset, 0.15f));
        }
        for (int i = 0; i < hours.size(); i++) {
            voters.add(new BarEntry(Integer.parseInt(hours.get(i).getHour()) + xOffset, hours.get(i).getCount()));
        }
    }
}
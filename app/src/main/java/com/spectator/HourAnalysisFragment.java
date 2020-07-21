package com.spectator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HourAnalysisFragment extends Fragment {

    private BarChart barChart;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hour_analysis_fragment, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = (BarChart) view.findViewById(R.id.chart);
        barChart.setMaxVisibleValueCount(24);
        barChart.setContentDescription("");
        Description desc = new Description();
        desc.setText(" ");
        barChart.setDescription(desc);
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        ArrayList<String> year = new ArrayList<>();

        year.add("8");
        year.add("9");
        year.add("10");
        year.add("11");
        year.add("12");
        year.add("13");
        year.add("14");
        year.add("15");
        year.add("16");
        year.add("17");
        year.add("18");
        year.add("19");
        year.add("20");
        year.add("21");

        ValueFormatter xAxisFormatter = new IndexAxisValueFormatter(year);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1); // only intervals of 1 day
        xAxis.setLabelCount(24);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextSize(15);
        xAxis.setTextColor(Color.WHITE);
        //xAxis.center
        //xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(xAxisFormatter);

        ArrayList<BarEntry> NoOfEmp = new ArrayList<>();

        NoOfEmp.add(new BarEntry(1, 3));
        NoOfEmp.add(new BarEntry(2, 4));
        NoOfEmp.add(new BarEntry(3, 4));
        NoOfEmp.add(new BarEntry(4, 3));
        NoOfEmp.add(new BarEntry(5, 6));
        NoOfEmp.add(new BarEntry(6, 8));
        NoOfEmp.add(new BarEntry(7, 12));
        NoOfEmp.add(new BarEntry(8, 15));
        NoOfEmp.add(new BarEntry(9, 17));
        NoOfEmp.add(new BarEntry(10, 16));
        NoOfEmp.add(new BarEntry(11, 18));
        NoOfEmp.add(new BarEntry(12, 13));
        NoOfEmp.add(new BarEntry(13, 9));

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
        barChart.animateY(2000);
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.rgb("#0037EF"));
        barChart.setData(data);

    }
}
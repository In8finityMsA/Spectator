package com.spectator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

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

        /*ArrayList NoOfEmp = new ArrayList();

        NoOfEmp.add(new BarEntry(945f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(1133f, 2));
        NoOfEmp.add(new BarEntry(1240f, 3));
        NoOfEmp.add(new BarEntry(1369f, 4));
        NoOfEmp.add(new BarEntry(1487f, 5));
        NoOfEmp.add(new BarEntry(1501f, 6));
        NoOfEmp.add(new BarEntry(1645f, 7));
        NoOfEmp.add(new BarEntry(1578f, 8));
        NoOfEmp.add(new BarEntry(1695f, 9));
        NoOfEmp.add(new BarEntry(1695f, 10));
        NoOfEmp.add(new BarEntry(1695f, 11));
        NoOfEmp.add(new BarEntry(1695f, 12));

        ArrayList year = new ArrayList();

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

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
        barChart.animateY(5000);
        BarData data = new BarData((IBarDataSet) year, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(data);*/

    }
}
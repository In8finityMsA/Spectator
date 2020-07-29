package com.spectator.detailedinfo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spectator.R;
import com.spectator.data.Hour;
import com.spectator.utils.JsonIO;

import java.io.File;
import java.util.ArrayList;

public class GraphsFragment extends Fragment {

    String date;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String hourlyJsonPath;

        Bundle extras = getArguments();
        if (extras == null) {
            Log.e("GraphsExtras", "null");
            hourlyJsonPath = "";
        }
        else {
            Log.i("GraphsExtras", "not null");
            date = extras.getString("date");
        }

        TextView exportData = (TextView) view.findViewById(R.id.export_data);

        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportData();
            }
        });

        ArrayList<Hour> hours = new ArrayList<>();
        ArrayList<BarEntry> voters = new ArrayList<>();

        JsonIO hourlyJsonIO = new JsonIO(getContext().getFilesDir(), date + ".hourly.json", Hour.ARRAY_KEY, false);
        hours = hourlyJsonIO.parseJsonArray(true, hours, true, Hour.ARRAY_KEY, Hour.class, Hour.constructorArgs, Hour.jsonKeys);

        settingValues(hours, voters);

        BarChart barChart = (BarChart) view.findViewById(R.id.chart);

        //Disabling interaction with the graph
        barChart.setTouchEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);

        //To avoid clipping xAxis labels
        barChart.setExtraOffsets(0, 0, 0, 10);

        //Turning off description and legend
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        //Setting xAxis. Text size 13; label color white; min step between labels is 1; draw labels at center of grid lines; turn off grid lines drawing.
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextSize(14);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(24);
        xAxis.setAvoidFirstLastClipping(true);

        //Setting yAxis. Start at 0; min step between labels is 1; label color white.
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setTextSize(12);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setAxisMinimum(0f);
        yAxisRight.setGranularity(1f);
        yAxisRight.setEnabled(false);

        BarDataSet bardataset = new BarDataSet(voters, null);
        //Animation (bars rising on start) duration
        barChart.animateY(2000);
        BarData data = new BarData(bardataset);
        //Turning on values above bars and setting value formatter
        data.setDrawValues(true);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(10);
        data.setValueFormatter(new CustomValueFormatter());
        //Setting color of bars
        bardataset.setColors(ColorTemplate.rgb("#0037EF"));
        barChart.setData(data);

    }

    private void exportData() {
        File file = new File(getContext().getFilesDir(), date + ".json");
        Uri uri = FileProvider.getUriForFile(getContext(), "com.spectator.fileProvider", file);

        Intent exportingIntent = new Intent(android.content.Intent.ACTION_SEND);
        exportingIntent.setType("application/json");
        exportingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        exportingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, date + " day voters list");
        exportingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(exportingIntent, "Export via"));
    }

    private void settingValues(ArrayList<Hour> hours, ArrayList<BarEntry> voters) {
        //Offset for xAxis to make bars between hour x and x+1, and not between x-1 and x
        int xOffset = 1;
        float minValue = 0.15f;

        //Finding min and max value of Hour String [0;23]
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
        //Filling gaps between min and max
        if (min != 25 && max != -1) {
            for (int i = min + 1; i < max; i++) {
                voters.add(new BarEntry(i + xOffset, minValue));
            }
        }
        //Setting data from hours array
        for (int i = 0; i < hours.size(); i++) {
            voters.add(new BarEntry(Integer.parseInt(hours.get(i).getHour()) + xOffset, hours.get(i).getCount()));
        }
    }

    private static class CustomValueFormatter extends ValueFormatter {
        @Override
        public String getBarLabel(BarEntry barEntry) {
            if (barEntry.getY() >= 1)
                return String.valueOf((int)barEntry.getY());
            else
                return "";
        }
    }
}
package com.spectator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HourAnalysisFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hour_analysis_fragment, container, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText("Graphs");
        return view;
    }

}
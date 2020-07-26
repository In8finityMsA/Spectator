package com.spectator.counter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spectator.R;

import java.util.Objects;

public class AdditionalInfoFragment extends Fragment {

    private TextView grandTotal;
    private TextView lastHour;
    private VerticalViewPager viewPager;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.additional_info_fragment, container, false);

        grandTotal = view.findViewById(R.id.total_amount);
        lastHour = view.findViewById(R.id.hourly_amount);
        viewPager = (VerticalViewPager) ((MainCounterScreen) this.getActivity()).getPager();

        Bundle extras = getArguments();
        if (extras == null) {
            Log.e("DetailedInfoExtras", "null");
        }
        else {
            Log.i("DetailedInfoExtras", "not null");
            grandTotal.setText(String.valueOf(extras.getInt("totally")));
            lastHour.setText(String.valueOf(extras.getInt("hourly")));
        }

        return view;
    }

    void setTotally(int totally) {
        if (grandTotal != null)
            this.grandTotal.setText(String.valueOf(totally));
    }

    void setHourly(int hourly) {
        if (lastHour != null)
            this.lastHour.setText(String.valueOf(hourly));
    }
}

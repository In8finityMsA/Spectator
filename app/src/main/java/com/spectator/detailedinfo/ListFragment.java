package com.spectator.detailedinfo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spectator.R;
import com.spectator.data.Voter;
import com.spectator.utils.ObjectWrapperForBinder;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private int totally;
    private int daily = 0;
    private int hourly = 0;
    private TextView total;
    private TextView lastHour;
    private TextView thisDay;
    private ArrayList<Voter> voters;
    private ScrollView scrollView;
    private LinearLayout scrollList;
    private boolean isPrevWhite = false;
    private LayoutInflater inflater;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();
        if (extras == null) {
            Log.e("extras", "null");
            totally = 0;

        }
        else {
            Log.i("extras", "not null");
            totally = extras.getInt("total");
            voters = (ArrayList<Voter>) ((ObjectWrapperForBinder)extras.getBinder("voters")).getData();
        }

        context = getContext();
        total = (TextView) view.findViewById(R.id.total);
        thisDay = (TextView) view.findViewById(R.id.daily);
        lastHour = (TextView) view.findViewById(R.id.hourly);

        scrollView = (ScrollView) view.findViewById(R.id.votes);
        scrollList = (LinearLayout) view.findViewById(R.id.scroll_list);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Initializing interface from voters array
        for (int j = 0 ; j < voters.size(); j++) {
            LinearLayout newRow = makeNewRow(voters.get(j));
            scrollList.addView(newRow);
            daily++;
        }
        thisDay.setText(String.valueOf(daily));
        total.setText(String.valueOf(daily));
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Turns off vote button if current date doesn't equals session date
        //TODO: make it date change indifferent
        checkVotesHourly();
        lastHour.setText(String.valueOf(hourly));
    }

    //Making new Linear layout for new vote
    private LinearLayout makeNewRow(Voter printVoter) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.rows, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(layoutParams);

        //Making color contrast for neighbouring layouts
        if (isPrevWhite) {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            isPrevWhite = false;
        }
        else {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundDark));
            isPrevWhite = true;
        }

        TextView newTimeView = linearLayout.findViewById(R.id.time);
        newTimeView.setText(printVoter.getFormattedTime());

        TextView newCountView = linearLayout.findViewById(R.id.count);
        newCountView.setText(String.valueOf(printVoter.getCount()));

        return linearLayout;
    }

    private void checkVotesHourly() {
        final long HOUR = 1000 * 60 * 60;
        hourly = 0;
        Log.e("check", String.valueOf(voters.size()));
        for (int i = voters.size() - 1; i >= 0; i--) {
            long currentTime = System.currentTimeMillis();
            long difference =  currentTime - voters.get(i).getTimestamp();
            if (difference < HOUR) {
                hourly++;
            }
            else {
                break;
            }
        }
    }

}

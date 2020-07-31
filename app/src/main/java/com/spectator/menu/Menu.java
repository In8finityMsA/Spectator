package com.spectator.menu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spectator.BaseActivity;
import com.spectator.data.Day;
import com.spectator.utils.DateFormatter;
import com.spectator.utils.JsonIO;
import com.spectator.counter.MainCounterScreen;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.R;
import com.spectator.utils.PreferencesIO;

import java.util.ArrayList;
import java.util.Locale;

public class Menu extends BaseActivity {

    private TextView addNew;
    private TextView todayDate;
    private TextView electionsDay;
    private int totally;
    private JsonIO daysJsonIO;
    private PreferencesIO preferencesIO;
    private ArrayList<Day> days;
    private LinearLayout scrollList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferencesIO = new PreferencesIO(this);
        daysJsonIO = new JsonIO(getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY, true);

        scrollList = findViewById(R.id.scroll_layout);
        addNew = findViewById(R.id.addNew);

        todayDate = findViewById(R.id.today_date);
        electionsDay = findViewById(R.id.elections_day);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Dialog.class);

                //Creating new Day record and writing it to the file
                //Day newDay = new Day(System.currentTimeMillis(), 0);
                //daysJsonIO.writeToEndOfFile(newDay.toJSONObject());

                //Passing date, total votes and daysJsonIO to Voting Activity
                final Bundle bundle = new Bundle();
                bundle.putBinder("daysJsonIO", new ObjectWrapperForBinder(daysJsonIO));
                //bundle.putString("date", newDay.getFormattedDate());
                bundle.putInt("total", totally);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Menu", "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Menu", "onResume");

        Locale locale;
        switch (preferencesIO.getInt(PreferencesIO.LANG_RADIOBUTTON_INDEX, 1)) {
            case 0: locale = new Locale("en"); break;
            case 1: locale = new Locale("ru"); break;
            case 2: locale = new Locale("be"); break;
            default: locale = new Locale("ru");
        }
        String str = getString(R.string.today) + DateFormatter.formatDate(System.currentTimeMillis(), "d MMMM", locale);
        //TODO: make it on date change action
        todayDate.setText(str);
        electionsDay.setText(getElectionStage());

        //TODO: maybe rework this, too dumb deleting everything
        //Parsing json file and building interface
        days = daysJsonIO.parseJsonArray(true, days, true, Day.ARRAY_KEY, Day.class, Day.constructorArgs2, Day.jsonKeys2, Day.defValues);
        //Counting total amount of votes
        totally = 0;
        for (Day day: days) {
            totally += day.getCount();
        }
        if (scrollList.getChildCount() > 1)
            scrollList.removeViews(0, scrollList.getChildCount() - 1);

        addNew.setVisibility(TextView.VISIBLE);
        for (int i = 0; i < days.size(); i++) {
            Log.i("daysCount", String.valueOf(days.get(i).getCount()));
            scrollList.addView(makeNewRow(days.get(i)), i);
            Log.i("daysDate", days.get(i).getFormattedDate());
            //Hiding button addNew if current date is already exists
            if (days.get(i).getFormattedDate().equals(DateFormatter.formatDate(System.currentTimeMillis()))) {
                addNew.setVisibility(TextView.GONE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Menu", "onStop");
    }

    private LinearLayout makeNewRow(final Day printDay) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.day_layout, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.setMargins(convertDpToPixel(this,10), convertDpToPixel(this, 10), convertDpToPixel(this, 10), 0);
        linearLayout.setLayoutParams(layoutParams);

        TextView newDateView = linearLayout.findViewById(R.id.date);
        newDateView.setText(printDay.getFormattedDate());

        TextView newCountView = linearLayout.findViewById(R.id.day_votes);
        newCountView.setText(String.valueOf(printDay.getCount()));

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainCounterScreen.class);
                final Bundle bundle = new Bundle();
                bundle.putBinder("daysJsonIO", new ObjectWrapperForBinder(daysJsonIO));
                bundle.putSerializable("day", printDay);
                bundle.putInt("total", totally);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return linearLayout;
    }

    private static int convertDpToPixel(Context context, float dp) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private String getElectionStage() {
        String currentDate = DateFormatter.formatDate(System.currentTimeMillis());
        //Log.i("currentDate", currentDate);

        switch (currentDate) {
            case "04.08.2020":
                return getString(R.string.first_day);
            case "05.08.2020" :
                return getString(R.string.second_day);
            case "06.08.2020" :
                return getString(R.string.third_day);
            case "07.08.2020" :
                return getString(R.string.fourth_day);
            case "08.08.2020" :
                return getString(R.string.fifth_day);
            case "09.08.2020" :
                return getString(R.string.primary_day);
            default:
                return getString(R.string.election_days);
        }
    }

}

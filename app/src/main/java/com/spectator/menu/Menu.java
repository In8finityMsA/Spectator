package com.spectator.menu;

import android.app.Activity;
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

import androidx.appcompat.app.AppCompatActivity;

import com.spectator.BaseActivity;
import com.spectator.data.Day;
import com.spectator.utils.DateFormatter;
import com.spectator.utils.JsonIO;
import com.spectator.counter.MainCounterScreen;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.R;

import java.util.ArrayList;

public class Menu extends BaseActivity {

    private TextView addNew;
    private TextView todayDate;
    private TextView electionsDay;
    private int totally;
    private JsonIO daysJsonIO;
    private ArrayList<Day> days;
    private LinearLayout scrollList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        daysJsonIO = new JsonIO(getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY, true);

        scrollList = findViewById(R.id.scroll_layout);
        addNew = findViewById(R.id.addNew);

        todayDate = findViewById(R.id.today_date);
        electionsDay = findViewById(R.id.elections_day);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Dialog.class);
                final Bundle bundle = new Bundle();
                bundle.putBinder("daysJsonIO", new ObjectWrapperForBinder(daysJsonIO));
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
        //TODO: make it on date change action
        String str = "Сегодня:  " + DateFormatter.formatDate(System.currentTimeMillis(), "d MMMM");
        todayDate.setText(str);
        electionsDay.setText(getElectionStage());

        //TODO: maybe rework this, too dumb deleting everything
        //Parsing json file and building interface
        days = daysJsonIO.parseJsonArray(true, days, true, Day.ARRAY_KEY, Day.class, Day.constructorArgs, Day.jsonKeys);
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
            //Log.i("currentDate", DateFormatter.formatDate(System.currentTimeMillis()));
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
                bundle.putString("date", printDay.getFormattedDate());
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

    private static String getElectionStage() {
        String currentDate = DateFormatter.formatDate(System.currentTimeMillis());
        //Log.i("currentDate", currentDate);

        switch (currentDate) {
            case "04.08.2020":
                return "Первый день досрочного голосования";
            case "05.08.2020" :
                return "Второй день досрочного голосования";
            case "06.08.2020" :
                return "Третий день досрочного голосования";
            case "07.08.2020" :
                return "Четвертый день досрочного голосования";
            case "08.08.2020" :
                return "Пятый день досрочного голосования";
            case "09.08.2020" :
                return "Основной день голосования";
            default:
                return "Голосование проходит с 4 по 9 августа";
        }
    }

}

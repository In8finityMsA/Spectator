package com.spectator.menu;

import android.content.Context;
import android.content.Intent;
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

import com.spectator.MainCounterScreen;
import com.spectator.ObjectWrapperForBinder;
import com.spectator.R;
import com.spectator.JsonIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Menu extends AppCompatActivity {

    private TextView addNew;
    private int totally;
    private JsonIO daysJsonIO;
    private ArrayList<Day> days;
    private LinearLayout scrollList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        daysJsonIO = new JsonIO(getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY);
        scrollList = findViewById(R.id.scroll_layout);
        addNew = findViewById(R.id.addNew);

        days = new ArrayList<Day>();

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

        //TODO: maybe rework this, too dumb
        //Parsing json file and building interface (also counting total amount of votes in parsing, not really good)
        parseJson(daysJsonIO.read(), true);
        if (scrollList.getChildCount() > 1)
            scrollList.removeViews(0, scrollList.getChildCount() - 1);

        addNew.setVisibility(TextView.VISIBLE);
        for (int i = 0; i < days.size(); i++) {
            Log.e("days", Integer.toString(days.size()));
            scrollList.addView(makeNewRow(days.get(i)), i);
            if (days.get(i).getFormattedDate().equals(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(System.currentTimeMillis()))) {
                addNew.setVisibility(TextView.GONE);
            }
        }
    }

    private LinearLayout makeNewRow(Day printDay) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.day_layout, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.setMargins(convertDpToPixel(this,20), convertDpToPixel(this, 15), convertDpToPixel(this, 20), 0);
        linearLayout.setLayoutParams(layoutParams);

        TextView newDateView = linearLayout.findViewById(R.id.date);
        newDateView.setText(printDay.getFormattedDate());

        TextView newCountView = linearLayout.findViewById(R.id.day_votes);
        newCountView.setText(String.format(Locale.GERMAN,"%d", printDay.getCount()));

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainCounterScreen.class);
                TextView textView = (TextView) view.findViewById(R.id.date);
                final Bundle bundle = new Bundle();
                bundle.putBinder("daysJsonIO", new ObjectWrapperForBinder(daysJsonIO));
                bundle.putString("date", textView.getText().toString());
                bundle.putInt("total", totally);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return linearLayout;
    }

    public void parseJson(JSONObject response, boolean isRewrite) {
        if (isRewrite) {
            days = new ArrayList<Day>();
        }
        totally = 0;
        try {
            JSONArray jsonArray = response.getJSONArray(Day.ARRAY_KEY);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String date = jsonObject.getString(Day.dateKey);
                int count = jsonObject.getInt(Day.countKey);
                totally += count;

                days.add(new Day(date, count));
            }

        } catch (JSONException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    static int convertDpToPixel(Context context, float dp){
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}

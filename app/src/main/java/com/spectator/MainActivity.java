package com.spectator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button voteButton;
    private int counter = 0;
    private int hourly = 0;
    private TextView total;
    private TextView lastHour;
    private String jsonPath = "log.json";
    private JSONObject jsonFile;
    private WorkWithJson jsonIO;
    private TextView count;
    private TextView time;
    private ArrayList<Voter> voters = new ArrayList<>();
    private ArrayList<LinearLayout> rowsList;
    private ScrollView scrollView;
    private LinearLayout scrollList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voteButton = (Button) findViewById(R.id.button);
        total = (TextView) findViewById(R.id.total);
        lastHour = (TextView) findViewById(R.id.hourly);

        time = (TextView) findViewById(R.id.time);
        count = (TextView) findViewById(R.id.count);

        scrollView = (ScrollView) findViewById(R.id.votes);
        scrollList = (LinearLayout) findViewById(R.id.scroll_list);
        rowsList = new ArrayList<LinearLayout>();
        voters = new ArrayList<Voter>();

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Reading from file on startup
        jsonIO = new WorkWithJson(this.getFilesDir(), jsonPath);
        try {
            voters = jsonIO.firstInit();
        } catch (JSONException e) {
            e.printStackTrace();
            voters = new ArrayList<Voter>();
        }
        //And initializing interface from voters array
        for (int j = 0 ; j < voters.size(); j++) {
            rowsList.add(makeNewRow(voters.get(j)));
            scrollList.addView(rowsList.get(rowsList.size() - 1));
            counter++;
        }

        total.setText(String.format(Locale.GERMAN, "%d", counter));

        //Timer for checking votes those are one hour old
        /*ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                checkVotesHourly();
                Log.e("Repeat", "rep 60 sec");

            }
        }, 60, 60, TimeUnit.SECONDS);*/

        //Vote button function
        voteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Creating new voter
                Voter newVoter = new Voter(System.currentTimeMillis(), ++counter);
                //Adding it to the list
                voters.add(newVoter);
                //Adding it to the json file Object
                //jsonIO.addVoterToJSON(newVoter, "voters");
                //Writing Object to json file
                //jsonIO.writeToFile();
                //Writing new voter to the end of json file
                jsonIO.writeToEndOfFile(newVoter);

                //Creating Linear Layout record for new voter
                if (voters.size() > 0){
                    rowsList.add(makeNewRow(newVoter));
                    scrollList.addView(rowsList.get(rowsList.size() - 1));
                    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    });

                }

                total.setText(String.format(Locale.GERMAN, "%d", counter));
                lastHour.setText(String.format(Locale.GERMAN, "%d", ++hourly));

        }
    });


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVotesHourly();
        lastHour.setText(String.format(Locale.GERMAN, "%d", hourly));
    }

    private LinearLayout makeNewRow(Voter printVoter) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.rows, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(layoutParams);

        TextView newTimeView = linearLayout.findViewById(R.id.time);
        newTimeView.setText(printVoter.getFormattedTime());

        TextView newCountView = linearLayout.findViewById(R.id.count);
        newCountView.setText(String.format(Locale.GERMAN,"%d", printVoter.getCount()));

        return linearLayout;
    }

    private void checkVotesHourly() {
        final long HOUR = 1000 * 60 * 60;
        hourly = 0;
        Log.e("check", String.format(Locale.GERMAN, "%d", voters.size()));
        for (int i = voters.size() - 1; i >= 0; i--) {
            Log.e("index", String.format(Locale.GERMAN, "%d", i));
            long currentTime = System.currentTimeMillis();
            long difference =  currentTime - voters.get(i).getTime();
            Log.e("diff", String.format(Locale.GERMAN, "%1$d - %2$d = %3$d", currentTime, voters.get(i).getTime(), difference));
            if (difference < HOUR) {
                hourly++;
            }
            else {
                break;
            }
        }
    }

    public float convertDpToPixel(float dp){
        return dp * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
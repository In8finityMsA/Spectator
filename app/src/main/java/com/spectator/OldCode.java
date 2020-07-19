package com.spectator;

import android.content.Context;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.spectator.menu.Day;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OldCode extends AppCompatActivity {

    private Button voteButton;
    private int totally;
    private int daily = 0;
    private int hourly = 0;
    private TextView total;
    private TextView lastHour;
    private TextView thisDay;
    private String jsonPath;
    private String date;
    private JsonIO votesJsonIO;
    private JsonIO daysJsonIO;
    private ArrayList<Voter> voters = new ArrayList<>();
    private ArrayList<LinearLayout> rowsList;
    private ScrollView scrollView;
    private LinearLayout scrollList;
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "null");
            date = "01.01.1970";
            totally = 0;
            jsonPath = "log.json";
        }
        else {
            Log.e("extras", "not null");
            date = extras.getString("date");
            totally = extras.getInt("total");
            jsonPath = date + ".json";
            daysJsonIO = (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
        }

        setContentView(R.layout.activity_main);
        voteButton = (Button) findViewById(R.id.button);
        total = (TextView) findViewById(R.id.total);
        thisDay = (TextView) findViewById(R.id.daily);
        lastHour = (TextView) findViewById(R.id.hourly);

        scrollView = (ScrollView) findViewById(R.id.votes);
        scrollList = (LinearLayout) findViewById(R.id.scroll_list);
        rowsList = new ArrayList<LinearLayout>();
        voters = new ArrayList<Voter>();


        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        //Reading from file on startup
        votesJsonIO = new JsonIO(this.getFilesDir(), jsonPath, "voters");
        parseJson(votesJsonIO.read(), true);

        //And initializing interface from voters array
        for (int j = 0 ; j < voters.size(); j++) {
            LinearLayout newRow = makeNewRow(voters.get(j));
            rowsList.add(newRow);
            scrollList.addView(newRow);
            daily++;
        }
        thisDay.setText(String.format(Locale.GERMAN, "%d", daily));
        total.setText(String.format(Locale.GERMAN, "%d", totally));

        //Listener for scrolling down scrollview when a vote is added
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
                Voter newVoter = new Voter(System.currentTimeMillis(), ++daily);
                //Adding it to the list
                voters.add(newVoter);
                //Writing new voter to the end of json file
                votesJsonIO.writeToEndOfFile(newVoter.toJSONObject());

                //Creating Linear Layout record for new voter
                if (voters.size() > 0) {
                    LinearLayout newRow = makeNewRow(newVoter);
                    rowsList.add(newRow);
                    scrollList.addView(newRow);
                }

                //Changing number of votes in TextViews
                thisDay.setText(String.format(Locale.GERMAN, "%d", daily));
                lastHour.setText(String.format(Locale.GERMAN, "%d", ++hourly));
                total.setText(String.format(Locale.GERMAN, "%d", ++totally));

                daysJsonIO.replaceObject(new Day(date, daily).toJSONObject(), Day.dateKey, date);

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        //Turns off vote button if current date doesn't equals session date
        //TODO: make it date change indifferent
        if (!date.equals(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(System.currentTimeMillis()))) {
            voteButton.setClickable(false);
            voteButton.setText("Voting ended");
            hourly = 0;
            lastHour.setText(String.format(Locale.GERMAN, "%d", hourly));
        }
        //Else checks hourly votes
        else {
            checkVotesHourly();
            lastHour.setText(String.format(Locale.GERMAN, "%d", hourly));
        }
    }

    //Making new Linear layout for new vote
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
            long difference =  currentTime - voters.get(i).getTimestamp();
            Log.e("diff", String.format(Locale.GERMAN, "%1$d - %2$d = %3$d", currentTime, voters.get(i).getTimestamp(), difference));
            if (difference < HOUR) {
                hourly++;
            }
            else {
                break;
            }
        }
    }

    public void parseJson(JSONObject response, boolean isRewrite) {
        if (isRewrite) {
            voters = new ArrayList<Voter>();
        }
        try {
            JSONArray stageArray = response.getJSONArray("voters");

            for (int i = 0; i < stageArray.length(); i++) {
                JSONObject jsonStage = stageArray.getJSONObject(i);

                long timestamp = jsonStage.getLong("timestamp");
                int count = jsonStage.getInt("count");

                voters.add(new Voter(timestamp, count));
            }

        } catch (JSONException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

}

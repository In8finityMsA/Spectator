package com.spectator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.spectator.menu.Day;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainCounterScreen extends AppCompatActivity {

    private TextView voteButton;
    private LinearLayout deleteLastButton;
    private LinearLayout markLastButton;
    private LinearLayout detailedInfo;
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
    private ArrayList<Voter> voters;
    private Handler hourlyCheckHandler;
    private boolean isHourlyCheckRunning;
    private float mProgress = 0f;
    private ProgressBar progressBar;
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_counter2);

        UniversalPagerAdapter universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[] {new DailyFragment(), new DetailsFragment()}, new String[] {"Daily", "Other"}, null);
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(universalPagerAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "null");
            date = "01.01.1970";
            totally = 0;
            jsonPath = "log.json";
            daysJsonIO = new JsonIO(this.getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY);
        }
        else {
            Log.e("extras", "not null");
            date = extras.getString("date");
            totally = extras.getInt("total");
            jsonPath = date + ".json";
            daysJsonIO = (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
        }

        voteButton = (TextView) findViewById(R.id.counter);
        deleteLastButton = (LinearLayout) findViewById(R.id.delete_button);
        markLastButton = (LinearLayout) findViewById(R.id.mark_button);

        total = (TextView) findViewById(R.id.total_amount);
        thisDay = (TextView) findViewById(R.id.daily_amount);
        lastHour = (TextView) findViewById(R.id.hourly_amount);

        detailedInfo = (LinearLayout) findViewById(R.id.short_statistics);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        progressBar = (ProgressBar) inflater.inflate(R.layout.progress_bar, null);

        //Reading from file on startup
        new Thread(myThread).start();
        votesJsonIO = new JsonIO(this.getFilesDir(), jsonPath, "voters");
        parseJson(votesJsonIO.read(), true);

        //thisDay.setText(String.format(Locale.GERMAN, "%d", daily));
        //total.setText(String.format(Locale.GERMAN, "%d", totally));

        //Timer for checking votes those are one hour old
        hourlyCheckHandler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(isHourlyCheckRunning) {
                    checkVotesHourly();
                    //lastHour.setText(String.format(Locale.GERMAN, "%d", hourly));

                    hourlyCheckHandler.sendEmptyMessageDelayed(0, 60000);
                }
            }
        };

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

                //Changing number of votes in TextViews
                //thisDay.setText(String.format(Locale.GERMAN, "%d", daily));
               // lastHour.setText(String.format(Locale.GERMAN, "%d", ++hourly));
                //total.setText(String.format(Locale.GERMAN, "%d", ++totally));

                daysJsonIO.replaceObject(new Day(date, daily).toJSONObject(), Day.dateKey, date);

            }
        });

        //Delete last button function
        deleteLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voters.size() > 0) {
                    votesJsonIO.deleteLast();
                    voters.remove(voters.size() - 1);

                    //Update number of votes in TextViews
                    if (hourly > 0) {
                        lastHour.setText(String.format(Locale.GERMAN, "%d", --hourly));
                    }
                    thisDay.setText(String.format(Locale.GERMAN, "%d", --daily));
                    total.setText(String.format(Locale.GERMAN, "%d", --totally));

                    daysJsonIO.replaceObject(new Day(date, daily).toJSONObject(), Day.dateKey, date);
                }
            }
        });

        //Mark last button function
        markLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Call detailed info if clicked on votes numbers
        /*detailedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Details.class);
                Bundle bundle = new Bundle();
                bundle.putBinder("voters", new ObjectWrapperForBinder(voters));
                bundle.putInt("total", totally);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Turns off vote button if current date doesn't equals session date
        //TODO: make it date change indifferent
        if (!date.equals(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(System.currentTimeMillis()))) {
            voteButton.setClickable(false);
            voteButton.setText("Voting ended");
            deleteLastButton.setClickable(false);
            markLastButton.setClickable(false);
            hourly = 0;
            isHourlyCheckRunning = false;
            hourlyCheckHandler.removeMessages(0);
            //lastHour.setText(String.format(Locale.GERMAN, "%d", hourly));
        }
        //Else checks hourly votes
        else {
            isHourlyCheckRunning = true;
            hourlyCheckHandler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        isHourlyCheckRunning = false;
        hourlyCheckHandler.removeMessages(0);
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

    private Runnable myThread = new Runnable() {
        @Override
        public void run() {
            while ( (int)Math.ceil(mProgress) < 100) {
                Log.e("progress", Float.toString(mProgress));
                try {
                    myHandler.sendMessage(myHandler.obtainMessage());
                    Thread.sleep(100);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            progressBar.setVisibility(View.GONE);
        }

        Handler myHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setProgress((int) mProgress);
            }
        };
    };

    public void parseJson(JSONObject response, boolean isRewrite) {
        if (isRewrite) {
            voters = new ArrayList<Voter>();
        }
        try {
            JSONArray stageArray = response.getJSONArray("voters");
            float addition = 0;
            if (stageArray.length() > 0)
                addition = (float) 100 / stageArray.length();
            else {
                mProgress = 100;
            }

            for (int i = 0; i < stageArray.length(); i++) {
                JSONObject jsonStage = stageArray.getJSONObject(i);
                mProgress += addition;
                daily++;

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
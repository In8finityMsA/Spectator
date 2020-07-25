package com.spectator.counter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.spectator.data.Hour;
import com.spectator.detailedinfo.Details;
import com.spectator.utils.DateFormatter;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.R;
import com.spectator.utils.UniversalPagerAdapter;
import com.spectator.utils.JsonIO;
import com.spectator.data.Day;
import com.spectator.data.Voter;

import org.json.JSONException;
import org.json.JSONObject;

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
    private VerticalViewPager viewPager;
    private DailyInfoFragment dailyInfoFragment;
    private AdditionalInfoFragment additionalInfoFragment;
    private String votersJsonPath;
    private String hourlyJsonPath;
    private String date;
    private JsonIO votersJsonIO;
    private JsonIO hourlyJsonIO;
    private JsonIO daysJsonIO;
    private ArrayList<Voter> voters;
    private Handler hourlyCheckHandler;
    private boolean isHourlyCheckRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_counter2);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "null");
            date = "01.01.1970";
            totally = 0;
            votersJsonPath = "log.json";
            daysJsonIO = new JsonIO(this.getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY, true);
        }
        else {
            Log.i("extras", "not null");
            date = extras.getString("date");
            totally = extras.getInt("total");
            votersJsonPath = date + ".json";
            hourlyJsonPath = date + ".hourly.json";
            daysJsonIO = (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
        }

        voteButton = (TextView) findViewById(R.id.count);
        deleteLastButton = (LinearLayout) findViewById(R.id.delete_button);
        markLastButton = (LinearLayout) findViewById(R.id.mark_button);
        detailedInfo = (LinearLayout) findViewById(R.id.info_button);

        TextView electionStatus = (TextView) findViewById(R.id.election_status);
        electionStatus.setText(date);

        //Reading from file on startup, init daily votes number
        votersJsonIO = new JsonIO(this.getFilesDir(), votersJsonPath, Voter.ARRAY_KEY, true);
        voters = votersJsonIO.parseJsonArray(false, new ArrayList<Voter>(), true, Voter.ARRAY_KEY, Voter.class, Voter.constructorArgs, Voter.jsonKeys);
        daily = voters.size();

        //Creating jsonIO for further distributing voters by their hours
        hourlyJsonIO = new JsonIO(this.getFilesDir(), hourlyJsonPath, Hour.ARRAY_KEY, true);

        //Creating fragments for voters numbers and view pager
        viewPager = findViewById(R.id.pager);
        dailyInfoFragment = new DailyInfoFragment();
        additionalInfoFragment = new AdditionalInfoFragment();

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt("totally", totally);
        fragmentBundle.putInt("daily", daily);
        //checking last hour votes. It's not really good because on startup it is performed twice (see onResume). But it's needed for fragments initialization
        checkVotesHourly();
        fragmentBundle.putInt("hourly", hourly);

        UniversalPagerAdapter universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[] {dailyInfoFragment, additionalInfoFragment}, new String[] {"Daily", "Other"}, fragmentBundle);
        viewPager.setAdapter(universalPagerAdapter);

        //Timer for checking votes those are one hour old
        hourlyCheckHandler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(isHourlyCheckRunning) {
                    checkVotesHourly();
                    additionalInfoFragment.setHourly(hourly);

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
                votersJsonIO.writeToEndOfFile(newVoter.toJSONObject());

                //Changing number of votes in TextViews
                dailyInfoFragment.setDaily(daily);
                additionalInfoFragment.setTotally(++totally);
                additionalInfoFragment.setHourly(++hourly);

                //Updating this day voters number in file
                try {
                    daysJsonIO.replaceObject(new Day(date, daily).toJSONObject(), Day.dateKey, date, Day.ARRAY_KEY);
                } catch (JsonIO.ObjectNotFoundException e) {
                    e.printStackTrace();
                }

                //Updating list with voters separated on hour basis (for graphs)
                String hourString = Hour.extractHourFromTime(newVoter.getFormattedTime());
                Log.i("HourlyFinal", hourString);
                try {
                    int index = hourlyJsonIO.getIndexOfObject(Hour.hourKey, hourString, Hour.ARRAY_KEY);
                    JSONObject object = hourlyJsonIO.searchObjectAtIndex(index, Hour.ARRAY_KEY);
                    object.put(Hour.countKey, object.getInt(Hour.countKey) + 1);
                    hourlyJsonIO.replaceObjectAtIndex(object, index, Hour.ARRAY_KEY);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonIO.ObjectNotFoundException e) {
                    hourlyJsonIO.writeToEndOfFile(new Hour(hourString, 1).toJSONObject());
                }
            }

        });

        //Delete last button function
        deleteLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deleting voter
                if (voters.size() > 0) {
                    votersJsonIO.deleteLast(Voter.ARRAY_KEY);
                    Voter deletedVoter = voters.remove(voters.size() - 1);

                    //Update number of votes in TextViews
                    if (hourly > 0) {
                        additionalInfoFragment.setHourly(--hourly);
                    }
                    dailyInfoFragment.setDaily(--daily);
                    additionalInfoFragment.setTotally(--totally);

                    //Updating this day voters number in file
                    try {
                        daysJsonIO.replaceObject(new Day(date, daily).toJSONObject(), Day.dateKey, date, Day.ARRAY_KEY);
                    } catch (JsonIO.ObjectNotFoundException e) {
                        e.printStackTrace();
                    }

                    //Updating list with voters separated on hour basis (for graphs)
                    String hourString = Hour.extractHourFromTime(deletedVoter.getFormattedTime());
                    Log.i("HourlyFinal", hourString);
                    try {
                        int index = hourlyJsonIO.getIndexOfObject(Hour.hourKey, hourString, Hour.ARRAY_KEY);
                        JSONObject object = hourlyJsonIO.searchObjectAtIndex(index, Hour.ARRAY_KEY);
                        if (object.getInt(Hour.countKey) > 0) {
                            object.put(Hour.countKey, object.getInt(Hour.countKey) - 1);
                            hourlyJsonIO.replaceObjectAtIndex(object, index, Hour.ARRAY_KEY);
                        }
                    } catch (JSONException | JsonIO.ObjectNotFoundException e) {
                        e.printStackTrace();
                    }

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
        detailedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Details.class);
                Bundle bundle = new Bundle();
                bundle.putBinder("voters", new ObjectWrapperForBinder(voters));
                bundle.putInt("total", totally);
                bundle.putString("hourlyJsonPath", hourlyJsonPath);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Turns off buttons if current date doesn't equals session date and stops hourly check
        //TODO: make it date change indifferent
        if (!date.equals(DateFormatter.formatDate(System.currentTimeMillis()))) {
            voteButton.setClickable(false);
            voteButton.setText("Voting ended");
            deleteLastButton.setClickable(false);
            markLastButton.setClickable(false);
            hourly = 0;
            isHourlyCheckRunning = false;
            hourlyCheckHandler.removeMessages(0);
            additionalInfoFragment.setHourly(hourly);
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

        //Stops hourly check
        isHourlyCheckRunning = false;
        hourlyCheckHandler.removeMessages(0);
    }

    //Checking how much votes made last hour
    private void checkVotesHourly() {
        final long HOUR = 1000 * 60 * 60;
        hourly = 0;
        Log.i("check", String.format(Locale.GERMAN, "%d", voters.size()));
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

    public ViewPager getPager(){
        return this.viewPager;
    }

}
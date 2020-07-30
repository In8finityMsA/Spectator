package com.spectator.counter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.spectator.BaseActivity;
import com.spectator.R;
import com.spectator.data.Comment;
import com.spectator.data.Day;
import com.spectator.data.Hour;
import com.spectator.data.Voter;
import com.spectator.detailedinfo.Details;
import com.spectator.utils.DateFormatter;
import com.spectator.utils.JsonIO;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.utils.PreferencesIO;
import com.spectator.utils.UniversalPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainCounterScreen extends BaseActivity {

    private TextView voteButton;
    private PreferencesIO preferencesIO;
    private LinearLayout deleteLastButton;
    private LinearLayout markLastButton;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("MainExtras", "null");
            date = "01.01.1970";
            totally = 0;
            votersJsonPath = "log.json";
            daysJsonIO = new JsonIO(this.getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY, true);
        }
        else {
            Log.i("MainExtras", "not null");
            date = extras.getString("date");
            totally = extras.getInt("total");
            //Day day = (Day) extras.getSerializable("day");
            //Log.i("MainDay", day.toString());
            votersJsonPath = date + ".json";
            hourlyJsonPath = date + ".hourly.json";
            daysJsonIO = (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
        }

        preferencesIO = new PreferencesIO(this);

        voteButton = (TextView) findViewById(R.id.count);
        deleteLastButton = (LinearLayout) findViewById(R.id.delete_button);
        markLastButton = (LinearLayout) findViewById(R.id.mark_button);
        LinearLayout detailedInfo = (LinearLayout) findViewById(R.id.info_button);
        LinearLayout done = (LinearLayout) findViewById(R.id.finish_button);
        ImageView doneIcon = (ImageView) findViewById(R.id.finish_icon);
        TextView doneLabel = (TextView) findViewById(R.id.finish_label);
        ImageView infoIcon = (ImageView) findViewById(R.id.info_icon);
        TextView infoLabel = (TextView) findViewById(R.id.info_label);

        final TextView yikNumber = (TextView) findViewById(R.id.precinct_id);
        String yikText = preferencesIO.getString(PreferencesIO.YIK_NUMBER, getString(R.string.yik_placeholder));
        if (!yikText.equals(getString(R.string.yik_placeholder))) {
            yikText = getString(R.string.yik_prefix) + yikText;
        }
        yikNumber.setText(yikText);
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

                int vibeId = preferencesIO.getInt(PreferencesIO.VIBE_RADIOBUTTON_INDEX, 2);

                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibe != null) {
                    switch (vibeId) {
                        case 0:
                            vibe.vibrate(500);
                            break;
                        case 1:
                            vibe.vibrate(250);
                            break;
                        case 2:
                            vibe.vibrate(100);
                            break;
                        case 3:
                            vibe.vibrate(50);
                            break;
                        case 4:
                            break;
                        default:
                            vibe.vibrate(100);
                            break;
                    }
                }


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
                //Log.i("HourlyFinal", hourString);
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
                Intent intent = new Intent(getApplicationContext(), EditTextDialog.class);
                final Bundle bundle = new Bundle();
                bundle.putBinder(EditTextDialog.jsonIOExtras, new ObjectWrapperForBinder(new JsonIO(getFilesDir(), Comment.COMMENTS_PATH, Comment.ARRAY_KEY, JsonIO.MODE.WRITE_ONLY_EOF, false)));
                bundle.putString(EditTextDialog.textHintExtras, getString(R.string.comment_hint));
                bundle.putInt(EditTextDialog.textInputTypeExtras, InputType.TYPE_CLASS_TEXT);
                bundle.putInt(EditTextDialog.textMaxLengthExtras, 500);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        yikNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditTextDialog.class);
                final Bundle bundle = new Bundle();
                bundle.putBinder(EditTextDialog.preferencesIOExtras, new ObjectWrapperForBinder(preferencesIO));
                bundle.putString(EditTextDialog.preferencesKeyExtras, PreferencesIO.YIK_NUMBER);
                bundle.putString(EditTextDialog.textHintExtras, getString(R.string.yik_hint));
                bundle.putInt(EditTextDialog.textInputTypeExtras, InputType.TYPE_CLASS_NUMBER);
                bundle.putInt(EditTextDialog.textMaxLengthExtras, 4);
                intent.putExtras(bundle);
                preferencesIO.setOnChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                        if (s.equals(PreferencesIO.YIK_NUMBER)) {
                            String yikText = preferencesIO.getString(PreferencesIO.YIK_NUMBER, getString(R.string.yik_placeholder));
                            if (!yikText.equals(getString(R.string.yik_placeholder))) {
                                yikText = getString(R.string.yik_prefix) + yikText;
                            }
                            yikNumber.setText(yikText);
                        }
                    }
                });
                startActivity(intent);
            }
        });

        //Call detailed info if clicked on votes numbers
        detailedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInfoClick();
            }
        });

        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInfoClick();
            }
        });

        infoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInfoClick();
            }
        });

        doneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClick();
            }
        });

        doneLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClick();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClick();
            }
        });

    }

    private void onDoneClick() {
        finish();
    }

    private void onInfoClick() {
        Intent intent = new Intent(getApplicationContext(), Details.class);
        Bundle bundle = new Bundle();
        bundle.putBinder("voters", new ObjectWrapperForBinder(voters));
        bundle.putInt("total", totally);
        bundle.putString("date", date);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Turns off buttons if current date doesn't equals session date and stops hourly check
        //TODO: make it date change indifferent
        if (!date.equals(DateFormatter.formatDate(System.currentTimeMillis()))) {
            voteButton.setClickable(false);
            voteButton.setText(R.string.end_voting);
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
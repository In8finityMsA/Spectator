package com.spectator.counter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

public class MainCounterScreen extends BaseActivity {

    private static final int EDIT_YIK_REQUEST = 11;
    private static final int EDIT_COMMENT_REQUEST = 12;

    private TextView voteButton;
    private PreferencesIO preferencesIO;
    private LinearLayout deleteLastButton;
    private LinearLayout markLastButton;
    private TextView yikNumber;
    private int totallyVoters = 0;
    private int totallyBands = 0;
    private Numbers votersNumbers;
    private Numbers bandsNumbers;
    private VerticalViewPager viewPager;
    private DailyInfoFragment dailyInfoFragment;
    private AdditionalInfoFragment additionalInfoFragment;
    private Day day;
    private JsonIO votersJsonIO;
    private JsonIO bandsJsonIO;
    private JsonIO hourlyVotersJsonIO;
    private JsonIO hourlyBandsJsonIO;
    private JsonIO daysJsonIO;
    private JsonIO commentsJsonIO;
    private ArrayList<Voter> voters;
    private ArrayList<Voter> bands;
    private Handler hourlyCheckHandler;
    private boolean isHourlyCheckRunning;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("MainExtras", "null");
            day = new Day((String) Day.defValues[0], (String) Day.defValues[1], (String) Day.defValues[2], (int) Day.defValues[3], (int) Day.defValues[4], (int) Day.defValues[5]);
            totallyVoters = 0;
            daysJsonIO = new JsonIO(this.getFilesDir(), Day.DAYS_PATH, Day.ARRAY_KEY, true);
        }
        else {
            Log.i("MainExtras", "not null");
            totallyVoters = extras.getInt("total");
            day = (Day) extras.getSerializable("day");
            daysJsonIO = (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
        }

        if (day == null)
            day = new Day((String) Day.defValues[0], (String) Day.defValues[1], (String) Day.defValues[2], (int) Day.defValues[3], (int) Day.defValues[4], (int) Day.defValues[5]);
        Log.i("MainDay", day.toString());

        if (day.getMode() == Day.PRESENCE) {
            setContentView(R.layout.main_counter2);
            initVoters();
        }
        else if (day.getMode() == Day.BANDS) {
            setContentView(R.layout.main_counter2);
            initBands();
        }
        else if (day.getMode() == Day.PRESENCE_BANDS) {
            setContentView(R.layout.main_counter2);
            initVoters();
            initBands();
        }

        preferencesIO = new PreferencesIO(this);

        voteButton = (TextView) findViewById(R.id.count);
        deleteLastButton = (LinearLayout) findViewById(R.id.delete_button);
        markLastButton = (LinearLayout) findViewById(R.id.mark_button);
        LinearLayout info = (LinearLayout) findViewById(R.id.info_button);
        LinearLayout done = (LinearLayout) findViewById(R.id.finish_button);
        ImageView doneIcon = (ImageView) findViewById(R.id.finish_icon);
        TextView doneLabel = (TextView) findViewById(R.id.finish_label);
        ImageView infoIcon = (ImageView) findViewById(R.id.info_icon);
        TextView infoLabel = (TextView) findViewById(R.id.info_label);

        yikNumber = (TextView) findViewById(R.id.precinct_id);
        if (day.getYik() != null)
            yikNumber.setText(String.format("%s%s", getString(R.string.yik_prefix), day.getYik()));
        else
            yikNumber.setText(R.string.yik_placeholder);

        TextView electionStatus = (TextView) findViewById(R.id.election_status);
        if (day.getFormattedDate() != null)
            electionStatus.setText(day.getFormattedDate());
        else
            electionStatus.setText("01.01.1970");

        //Creating JsonIO for comments writing
        commentsJsonIO = new JsonIO(getFilesDir(), Comment.COMMENTS_PATH, Comment.ARRAY_KEY, JsonIO.MODE.WRITE_ONLY_EOF, false);

        //Creating fragments for voters numbers and view pager
        viewPager = findViewById(R.id.pager);
        dailyInfoFragment = new DailyInfoFragment();
        additionalInfoFragment = new AdditionalInfoFragment();

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt("totally", votersNumbers.totally);
        fragmentBundle.putInt("daily", votersNumbers.daily);
        //checking last hour votes. It's not really good because on startup it is performed twice (see onResume). But it's needed for fragments initialization
        checkVotesHourly(voters, votersNumbers);
        fragmentBundle.putInt("hourly", votersNumbers.hourly);

        UniversalPagerAdapter universalPagerAdapter = new UniversalPagerAdapter(this, getSupportFragmentManager(), new Fragment[] {dailyInfoFragment, additionalInfoFragment}, new String[] {"Daily", "Other"}, fragmentBundle);
        viewPager.setAdapter(universalPagerAdapter);

        //Timer for checking votes those are one hour old
        hourlyCheckHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (isHourlyCheckRunning) {

                    if (day.getMode() == Day.PRESENCE_BANDS) {
                        checkVotesHourly(voters, votersNumbers);
                        checkVotesHourly(bands, bandsNumbers);
                        additionalInfoFragment.setHourly(votersNumbers.hourly);
                        additionalInfoFragment.setHourly(bandsNumbers.hourly);
                    }
                    else if (day.getMode() == Day.PRESENCE) {
                        checkVotesHourly(voters, votersNumbers);
                        additionalInfoFragment.setHourly(votersNumbers.hourly);
                    }
                    else if (day.getMode() == Day.BANDS) {
                        checkVotesHourly(bands, bandsNumbers);
                        additionalInfoFragment.setHourly(bandsNumbers.hourly);
                    }

                    hourlyCheckHandler.sendEmptyMessageDelayed(0, 60000);
                }
            }
        };

        //Vote button function
        voteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                doVibration();

                if (day.getMode() == Day.PRESENCE_BANDS) {
                    onAddRecord(voters, votersJsonIO, hourlyVotersJsonIO, votersNumbers);
                    onAddRecord(bands, bandsJsonIO, hourlyBandsJsonIO, bandsNumbers);
                }
                else if (day.getMode() == Day.PRESENCE) {
                    onAddRecord(voters, votersJsonIO, hourlyVotersJsonIO, votersNumbers);
                }
                else if (day.getMode() == Day.BANDS) {
                    onAddRecord(bands, bandsJsonIO, hourlyBandsJsonIO, bandsNumbers);
                }

            }

        });

        //Delete last button function
        deleteLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (day.getMode() == Day.PRESENCE_BANDS) {
                    onDeleteRecord(voters, votersJsonIO, hourlyVotersJsonIO, votersNumbers);
                    onDeleteRecord(bands, bandsJsonIO, hourlyBandsJsonIO, bandsNumbers);
                }
                else if (day.getMode() == Day.PRESENCE) {
                    onDeleteRecord(voters, votersJsonIO, hourlyVotersJsonIO, votersNumbers);
                }
                else if (day.getMode() == Day.BANDS) {
                    onDeleteRecord(bands, bandsJsonIO, hourlyBandsJsonIO, bandsNumbers);
                }
            }
        });

        //Mark last button function
        markLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditTextDialog.class);
                final Bundle bundle = new Bundle();
                bundle.putString(EditTextDialog.textHintExtras, getString(R.string.comment_hint));
                bundle.putInt(EditTextDialog.textInputTypeExtras, InputType.TYPE_CLASS_TEXT);
                bundle.putInt(EditTextDialog.textMaxLengthExtras, 500);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_COMMENT_REQUEST);
            }
        });

        yikNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditTextDialog.class);
                final Bundle bundle = new Bundle();
                bundle.putString(EditTextDialog.textHintExtras, getString(R.string.yik_hint));
                bundle.putInt(EditTextDialog.textInputTypeExtras, InputType.TYPE_CLASS_NUMBER);
                bundle.putInt(EditTextDialog.textMaxLengthExtras, 4);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_YIK_REQUEST);
            }
        });

        View.OnClickListener infoListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInfoClick();
            }
        };
        info.setOnClickListener(infoListener);
        infoIcon.setOnClickListener(infoListener);
        infoLabel.setOnClickListener(infoListener);

        View.OnClickListener doneListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClick();
            }
        };
        doneIcon.setOnClickListener(doneListener);
        doneLabel.setOnClickListener(doneListener);
        done.setOnClickListener(doneListener);

    }

    private void onDeleteRecord(ArrayList<Voter> records, JsonIO recordsJsonIO, JsonIO hourlyRecordsJsonIO, Numbers numbers) {
        if (records.size() > 0) {
            recordsJsonIO.deleteLast(Voter.ARRAY_KEY);
            Voter deletedVoter = records.remove(records.size() - 1);

            //Update number of votes in TextViews
            if (numbers.hourly > 0) {
                additionalInfoFragment.setHourly(--numbers.hourly);
            }
            dailyInfoFragment.setDaily(--numbers.daily);
            additionalInfoFragment.setTotally(--numbers.totally);

            //Updating this day voters number in file
            try {
                daysJsonIO.replaceObject(day.getDayWithChanged(numbers.daily, numbers.daily).toJSONObject(), Day.nameKey, day.getName(), Day.ARRAY_KEY);
            } catch (JsonIO.ObjectNotFoundException e) {
                e.printStackTrace();
            }

            //Updating list with voters separated on hour basis (for graphs)
            String hourString = Hour.extractHourFromTime(deletedVoter.getFormattedTime());
            Log.i("HourlyFinal", hourString);
            try {

                int index = hourlyRecordsJsonIO.getIndexOfObject(Hour.hourKey, hourString, Hour.ARRAY_KEY);
                JSONObject object = hourlyRecordsJsonIO.searchObjectAtIndex(index, Hour.ARRAY_KEY);
                if (object.getInt(Hour.countKey) > 0) {
                    object.put(Hour.countKey, object.getInt(Hour.countKey) - 1);
                    hourlyRecordsJsonIO.replaceObjectAtIndex(object, index, Hour.ARRAY_KEY);
                }
                else  {
                    hourlyRecordsJsonIO.deleteAt(index, Hour.ARRAY_KEY);
                }

            } catch (JSONException | JsonIO.ObjectNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void onAddRecord(ArrayList<Voter> records, JsonIO recordsJsonIO, JsonIO hourlyRecordsJsonIO, Numbers numbers) {
        //Creating new voter
        Voter newVoter = new Voter(System.currentTimeMillis(), ++numbers.daily);
        //Adding it to the list
        records.add(newVoter);
        //Writing new voter to the end of json file
        recordsJsonIO.writeToEndOfFile(newVoter.toJSONObject());

        //Changing number of votes in TextViews
        dailyInfoFragment.setDaily(numbers.daily);
        additionalInfoFragment.setTotally(++numbers.totally);
        additionalInfoFragment.setHourly(++numbers.hourly);

        //Updating this day voters number in file
        try {
            daysJsonIO.replaceObject(day.getDayWithChanged(numbers.daily, numbers.daily).toJSONObject(), Day.nameKey, day.getName(), Day.ARRAY_KEY);
        } catch (JsonIO.ObjectNotFoundException e) {
            e.printStackTrace();
        }

        //Updating list with voters separated on hour basis (for graphs)
        String hourString = Hour.extractHourFromTime(newVoter.getFormattedTime());
        //Log.i("HourlyFinal", hourString);
        try {
            int index = hourlyRecordsJsonIO.getIndexOfObject(Hour.hourKey, hourString, Hour.ARRAY_KEY);
            JSONObject object = hourlyRecordsJsonIO.searchObjectAtIndex(index, Hour.ARRAY_KEY);
            object.put(Hour.countKey, object.getInt(Hour.countKey) + 1);
            hourlyRecordsJsonIO.replaceObjectAtIndex(object, index, Hour.ARRAY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonIO.ObjectNotFoundException e) {
            hourlyRecordsJsonIO.writeToEndOfFile(new Hour(hourString, 1).toJSONObject());
        }
    }

    private void initVoters() {
        String votersJsonPath = day.getName() + ".voters" + ".json";
        String hourlyVotersJsonPath = day.getName() + ".voters" + ".hourly.json";
        //Reading from file on startup, init daily votes number
        votersJsonIO = new JsonIO(this.getFilesDir(), votersJsonPath, Voter.ARRAY_KEY, true);
        voters = votersJsonIO.parseJsonArray(false, new ArrayList<Voter>(), true, Voter.ARRAY_KEY, Voter.class, Voter.constructorArgs, Voter.jsonKeys, null);
        votersNumbers = new Numbers();
        votersNumbers.daily = voters.size();
        votersNumbers.totally = totallyVoters;

        //Creating jsonIO for further distributing voters by their hours
        hourlyVotersJsonIO = new JsonIO(this.getFilesDir(), hourlyVotersJsonPath, Hour.ARRAY_KEY, true);
    }

    private void initBands() {
        String bandsJsonPath = day.getName() + ".bands" + ".json";
        String hourlyBandsJsonPath = day.getName() + ".bands" + ".hourly.json";
        //Reading from file on startup, init daily votes number
        bandsJsonIO = new JsonIO(this.getFilesDir(), bandsJsonPath, Voter.ARRAY_KEY, true);
        bands = bandsJsonIO.parseJsonArray(false, new ArrayList<Voter>(), true, Voter.ARRAY_KEY, Voter.class, Voter.constructorArgs, Voter.jsonKeys, null);
        bandsNumbers = new Numbers();
        bandsNumbers.daily = bands.size();
        bandsNumbers.totally = totallyBands;

        //Creating jsonIO for further distributing voters by their hours
        hourlyBandsJsonIO = new JsonIO(this.getFilesDir(), hourlyBandsJsonPath, Hour.ARRAY_KEY, true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_YIK_REQUEST: {
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("textResult")) {
                        if (!data.getStringExtra("textResult").equals("")) {
                            day.setYik(data.getStringExtra("textResult"));
                            yikNumber.setText(String.format("%s%s", getString(R.string.yik_prefix), day.getYik()));
                            try {
                                daysJsonIO.replaceObject(day.toJSONObject(), Day.nameKey, day.getName(), Day.ARRAY_KEY);
                            } catch (JsonIO.ObjectNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            yikNumber.setText(getString(R.string.yik_placeholder));
                    }
                }
                break;
            }
            case EDIT_COMMENT_REQUEST: {
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("textResult")) {
                        if (!data.getStringExtra("textResult").equals("")) {
                            commentsJsonIO.writeToEndOfFile(new Comment(System.currentTimeMillis(), data.getStringExtra("textResult")).toJSONObject());
                        }
                    }
                }
            }
        }
    }

    private void onDoneClick() {
        finish();
    }

    private void onInfoClick() {
        Intent intent = new Intent(getApplicationContext(), Details.class);
        Bundle bundle = new Bundle();
        bundle.putBinder("voters", new ObjectWrapperForBinder(voters));
        bundle.putInt("total", votersNumbers.totally);
        bundle.putString("date", day.getFormattedDate());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Turns off buttons if current date doesn't equals session date and stops hourly check
        //TODO: make it date change indifferent
        if (!day.getFormattedDate().equals(DateFormatter.formatDate(System.currentTimeMillis()))) {
            voteButton.setClickable(false);
            voteButton.setText(R.string.end_voting);
            deleteLastButton.setClickable(false);
            markLastButton.setClickable(false);
            if (day.getMode() == Day.PRESENCE_BANDS) {
                votersNumbers.hourly = 0;
                additionalInfoFragment.setHourly(votersNumbers.hourly);
            }
            else if (day.getMode() == Day.PRESENCE) {
                votersNumbers.hourly = 0;
                additionalInfoFragment.setHourly(votersNumbers.hourly);
            }
            else if (day.getMode() == Day.BANDS) {
                bandsNumbers.hourly = 0;
                additionalInfoFragment.setHourly(bandsNumbers.hourly);
            }
            isHourlyCheckRunning = false;
            hourlyCheckHandler.removeMessages(0);

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
    private void checkVotesHourly(ArrayList<Voter> records, Numbers numbers) {
        final long HOUR = 1000 * 60 * 60;
        numbers.hourly = 0;
        Log.i("check", String.valueOf(records.size()));
        for (int i = records.size() - 1; i >= 0; i--) {
            long currentTime = System.currentTimeMillis();
            long difference =  currentTime - records.get(i).getTimestamp();
            if (difference < HOUR) {
                numbers.hourly++;
            }
            else {
                break;
            }
        }
    }

    private void doVibration() {
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
    }

    public ViewPager getPager(){
        return this.viewPager;
    }

    private static class Numbers {
        private int totally = 0;
        private int daily = 0;
        private int hourly = 0;
    }

}
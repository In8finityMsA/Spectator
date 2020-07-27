package com.spectator.menu;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.spectator.BaseActivity;
import com.spectator.R;
import com.spectator.utils.PreferencesIO;

public class Settings extends BaseActivity {

    private RadioGroup langRadioGroup;
    private Switch themeSwitch;
    private RadioGroup textRadioGroup;
    private PreferencesIO preferencesIO;
    private RadioGroup vibeSelection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        preferencesIO = new PreferencesIO(this);
        themeSwitch = (Switch) findViewById(R.id.theme_switch);
        langRadioGroup = findViewById(R.id.lang_selection);
        vibeSelection = findViewById(R.id.vibe_selection);

        LoadPreferences();

        themeSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferencesIO.putBoolean(PreferencesIO.IS_NIGHT_MODE, b);
            }
        });

        langRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.e("CheckedChange", "changed");
                RadioButton checkedRadioButton = (RadioButton) langRadioGroup.findViewById(checkedId);
                int checkedIndex = langRadioGroup.indexOfChild(checkedRadioButton);

                preferencesIO.putInt(PreferencesIO.LANG_RADIOBUTTON_INDEX, checkedIndex);
                preferencesIO.putBoolean(PreferencesIO.IS_RECREATE_START, true);
                recreate();
            }
        });

        vibeSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId2) {
                RadioButton checkedRadioButton = (RadioButton) vibeSelection.findViewById(checkedId2);
                int checkedIndex2 = vibeSelection.indexOfChild(checkedRadioButton);

                preferencesIO.putInt(PreferencesIO.VIBE_RADIOBUTTON_INDEX, checkedIndex2);


                switch (checkedId2) {
                    case R.id.vibe_max:
                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(500);
                    case R.id.vibe_high:
                        Vibrator vibe2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe2.vibrate(250);
                    case R.id.vibe_normal:
                        Vibrator vibe3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe3.vibrate(100);
                    case R.id.vibe_low:
                        Vibrator vibe4 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe4.vibrate(50);
                    case R.id.vibe_none:
                        Vibrator vibe5 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe5.vibrate(0);
                    default:
                        Vibrator vibe0 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe0.vibrate(100);
                }
            }
        });


        /*textRadioGroup = findViewById(R.id.text_selection);
        textRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton) textRadioGroup.findViewById(checkedId);
                int checkedIndex = textRadioGroup.indexOfChild(checkedRadioButton);

                SavePreferences(TEXT_RADIOBUTTON_INDEX, checkedIndex);
            }
        });*/

    }

    private void LoadPreferences() {
        int savedLangIndex = preferencesIO.getInt(PreferencesIO.LANG_RADIOBUTTON_INDEX, 0);
        RadioButton langCheckedRadioButton = (RadioButton) langRadioGroup.getChildAt(savedLangIndex);
        langCheckedRadioButton.setChecked(true);

        boolean isNightMode = preferencesIO.getBoolean(PreferencesIO.IS_NIGHT_MODE, true);
        themeSwitch.setChecked(isNightMode);

        int savedVibeIndex = preferencesIO.getInt(PreferencesIO.VIBE_RADIOBUTTON_INDEX, 2);
        RadioButton vibeCheckedRadioButton = (RadioButton) vibeSelection.getChildAt(savedVibeIndex);
        vibeCheckedRadioButton.setChecked(true);
        /*int savedTextIndex = sp.getInt(LANG_RADIOBUTTON_INDEX, 0);
        RadioButton textCheckedRadioButton = (RadioButton) textRadioGroup.getChildAt(savedTextIndex);
        textCheckedRadioButton.setChecked(true);*/
    }

}

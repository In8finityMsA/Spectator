package com.spectator.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.spectator.R;

public class Settings extends AppCompatActivity {

    private static final String MY_SETTINGS = "Settings";
    private static final String IS_FIRST_TIME = "Is first time";
    private static final String LANG_RADIOBUTTON_INDEX = "Saved language button index";
    private static final String IS_NIGHT_MODE = "Night mode";
    private static final String WALLPAPERS_INDEX = "Wallpaper";
    private final static String TEXT_RADIOBUTTON_INDEX = "Text size button index";
    private RadioGroup langRadioGroup;
    private Switch themeSwitch;
    private ViewPager viewPager;
    private RadioGroup textRadioGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        langRadioGroup = findViewById(R.id.lang_selection);
        langRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton) langRadioGroup.findViewById(checkedId);
                int checkedIndex = langRadioGroup.indexOfChild(checkedRadioButton);

                SavePreferences(LANG_RADIOBUTTON_INDEX, checkedIndex);
            }
        });
        themeSwitch = (Switch) findViewById(R.id.theme_switch);
        themeSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SavePreferences(IS_NIGHT_MODE, b);
            }
        });
        /*viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SavePreferences(WALLPAPERS_INDEX, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
        /*textRadioGroup = findViewById(R.id.text_selection);
        textRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton) textRadioGroup.findViewById(checkedId);
                int checkedIndex = textRadioGroup.indexOfChild(checkedRadioButton);

                SavePreferences(TEXT_RADIOBUTTON_INDEX, checkedIndex);
            }
        });*/

        LoadPreferences();

    }

    private void LoadPreferences() {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        int savedLangIndex = sp.getInt(LANG_RADIOBUTTON_INDEX, 0);
        RadioButton langCheckedRadioButton = (RadioButton) langRadioGroup.getChildAt(savedLangIndex);
        langCheckedRadioButton.setChecked(true);
        boolean isNightMode = sp.getBoolean(IS_NIGHT_MODE, false);
        themeSwitch.setChecked(isNightMode);
        /*int wallpapersIndex = sp.getInt(WALLPAPERS_INDEX, 0);
        viewPager.setCurrentItem(wallpapersIndex);*/
        /*int savedTextIndex = sp.getInt(LANG_RADIOBUTTON_INDEX, 0);
        RadioButton textCheckedRadioButton = (RadioButton) textRadioGroup.getChildAt(savedTextIndex);
        textCheckedRadioButton.setChecked(true);*/
    }

    private void SavePreferences(String key, int value) {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void SavePreferences(String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}

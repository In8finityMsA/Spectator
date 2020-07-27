package com.spectator;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spectator.utils.PreferencesIO;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private PreferencesIO preferencesIO;
    private SharedPreferences.OnSharedPreferenceChangeListener langListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesIO = new PreferencesIO(this);
        int localeIndex = preferencesIO.getInt(PreferencesIO.LANG_RADIOBUTTON_INDEX, 0);
        setLocale(localeIndex);

        /*langListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.e("OnSharedChangeBase", s);
                if (s.equals(PreferencesIO.IS_NIGHT_MODE)) {
                    int langIndex = sharedPreferences.getInt(PreferencesIO.LANG_RADIOBUTTON_INDEX, 0);
                    setLocale(langIndex);
                }
            }
        };
        preferencesIO.setOnChangeListener(langListener);*/
        //preferencesIO.deleteOnChangeListener();
    }

    private void setLocale(int localeIndex){
        Locale locale;
        Log.e("setLocale", String.valueOf(localeIndex));
        switch (localeIndex) {
            case 0:
                locale = new Locale("en");
                break;
            case 1:
                locale = new Locale("ru");
                break;
            case 2:
                locale = new Locale("be");
                break;
            default:
                locale = new Locale("en");
        }
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}

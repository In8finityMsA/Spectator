package com.spectator.menu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spectator.BaseActivity;
import com.spectator.R;
import com.spectator.utils.PreferencesIO;

import java.util.Locale;

public class Start extends BaseActivity {

    private TextView start;
    private TextView settings;
    private TextView aboutUs;
    private Locale locale;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        start = (TextView) findViewById(R.id.start);
        settings = (TextView) findViewById(R.id.settings);
        aboutUs = (TextView) findViewById(R.id.about_us);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
            }

        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AboutUs.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferencesIO preferencesIO = new PreferencesIO(this);
        if (preferencesIO.getBoolean(PreferencesIO.IS_RECREATE_START, false)) {
            preferencesIO.putBoolean(PreferencesIO.IS_RECREATE_START, false);
            recreate();
        }
    }
}

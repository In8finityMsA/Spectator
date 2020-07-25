package com.spectator.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spectator.R;

    public class Start extends AppCompatActivity {

        private TextView start;
        private TextView settings;
        private TextView aboutUs;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.start);

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



}

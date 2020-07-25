package com.spectator.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spectator.R;
import com.spectator.menu.Menu;

    public class Start extends AppCompatActivity {

        private TextView start;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.start);

            start = (TextView) findViewById(R.id.start);

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Menu.class);

                    startActivity(intent);
                }

            });
        }



}

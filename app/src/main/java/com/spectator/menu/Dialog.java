package com.spectator.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spectator.MainActivity;
import com.spectator.ObjectWrapperForBinder;
import com.spectator.R;
import com.spectator.JsonIO;

public class Dialog extends AppCompatActivity {

    private TextView noButton;
    private TextView yesButton;
    private JsonIO jsonIO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "null");
        }
        else {
            Log.e("extras", "not null");
            jsonIO =  (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("jsonIO")).getData();
        }

        yesButton = findViewById(R.id.yes);
        noButton = findViewById(R.id.no);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                //Creating new Day record and writing it to the file
                Menu.Day newDay = new Menu.Day(System.currentTimeMillis(), 0);
                jsonIO.writeToEndOfFile(newDay.toJSONObject());

                //Passing date and jsonIO to Voting Activity
                final Bundle bundle = new Bundle();
                bundle.putBinder("jsonIO", new ObjectWrapperForBinder(jsonIO));
                bundle.putString("date", newDay.getFormattedDate());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

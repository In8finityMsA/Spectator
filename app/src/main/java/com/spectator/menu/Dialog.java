package com.spectator.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spectator.counter.MainCounterScreen;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.R;
import com.spectator.data.Day;
import com.spectator.utils.JsonIO;

public class Dialog extends AppCompatActivity {

    private TextView noButton;
    private TextView yesButton;
    private JsonIO daysJsonIO;
    private int totally;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "null");
        }
        else {
            Log.i("extras", "not null");
            daysJsonIO =  (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
            totally = extras.getInt("total");
        }

        yesButton = findViewById(R.id.yes);
        noButton = findViewById(R.id.no);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainCounterScreen.class);

                //Creating new Day record and writing it to the file
                Day newDay = new Day(System.currentTimeMillis(), 0);
                daysJsonIO.writeToEndOfFile(newDay.toJSONObject());

                //Passing date, total votes and daysJsonIO to Voting Activity
                final Bundle bundle = new Bundle();
                bundle.putBinder("daysJsonIO", new ObjectWrapperForBinder(daysJsonIO));
                bundle.putString("date", newDay.getFormattedDate());
                bundle.putInt("total", totally);
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

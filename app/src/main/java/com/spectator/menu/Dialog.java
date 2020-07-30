package com.spectator.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spectator.BaseActivity;
import com.spectator.counter.MainCounterScreen;
import com.spectator.utils.DateFormatter;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.R;
import com.spectator.data.Day;
import com.spectator.utils.JsonIO;
import com.spectator.utils.PreferencesIO;

public class Dialog extends BaseActivity {

    private Button noButton;
    private Button yesButton;
    private EditText editName;
    private EditText editYikNumber;
    private CheckBox checkPresence;
    private CheckBox checkBands;
    private JsonIO daysJsonIO;
    private int totally;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("DialogExtras", "null");
        }
        else {
            Log.i("DialogExtras", "not null");
            daysJsonIO =  (JsonIO) ((ObjectWrapperForBinder)extras.getBinder("daysJsonIO")).getData();
            totally = extras.getInt("total");
        }

        yesButton = (Button) findViewById(R.id.yes);
        noButton = (Button) findViewById(R.id.no);
        editName = (EditText) findViewById(R.id.edit_name);
        editYikNumber = (EditText) findViewById(R.id.edit_yik_number);
        checkPresence = (CheckBox) findViewById(R.id.count_people);
        checkBands = (CheckBox) findViewById(R.id.count_bands);

        final String date = DateFormatter.formatDate(System.currentTimeMillis(), "dd.MM.yyyy");
        editName.setText(date);
        PreferencesIO preferencesIO = new PreferencesIO(this);
        editYikNumber.setText(preferencesIO.getString(PreferencesIO.YIK_NUMBER, ""));
        checkPresence.setChecked(true);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean canPass = true;
                if (editName.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT);
                    toast.show();
                    canPass = false;
                }
                else if (editYikNumber.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a PEC number", Toast.LENGTH_SHORT);
                    toast.show();
                    canPass = false;
                }
                else if (!checkPresence.isChecked() && !checkBands.isChecked()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please choose a mode", Toast.LENGTH_SHORT);
                    toast.show();
                    canPass = false;
                }

                if (canPass) {
                    Intent intent = new Intent(getApplicationContext(), MainCounterScreen.class);

                    //Creating new Day record and writing it to the file
                    Day newDay = new Day(date, 0);
                    daysJsonIO.writeToEndOfFile(newDay.toJSONObject());

                    //Passing date, total votes and daysJsonIO to Voting Activity
                    final Bundle bundle = new Bundle();
                    bundle.putBinder("daysJsonIO", new ObjectWrapperForBinder(daysJsonIO));
                    //bundle.putSerializable("day", newDay);
                    bundle.putString("date", newDay.getFormattedDate());
                    bundle.putInt("total", totally);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
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

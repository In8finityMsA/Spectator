package com.spectator.counter;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.spectator.BaseActivity;
import com.spectator.R;
import com.spectator.data.Comment;
import com.spectator.utils.JsonIO;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.utils.PreferencesIO;

public class EditTextDialog extends BaseActivity {

    public static final String jsonIOExtras = "jsonIO";
    public static final String jsonClassExtras = "Class to write in json";

    public static final String preferencesIOExtras = "preferencesIO";
    public static final String preferencesKeyExtras = "Key to write in preferences";

    public static final String textHintExtras = "Hint for edit text";
    public static final String textInputTypeExtras = "Input type of EditText";
    public static final String textMaxLengthExtras = "Max length of entered text";

    private JsonIO jsonIO;
    private PreferencesIO preferencesIO;
    private String preferencesStringKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_dialog);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String textHint = null;
        int inputType = 0;
        int maxLength = 0;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("EditTextDialogExtras", "null");
            finish();
        }
        else {
            Log.i("EditTextDialogExtras", "not null");
            if (extras.containsKey(jsonIOExtras)) {
                jsonIO = (JsonIO) ((ObjectWrapperForBinder) extras.getBinder(jsonIOExtras)).getData();
            }
            if (extras.containsKey(preferencesIOExtras) && extras.containsKey(preferencesKeyExtras)) {
                preferencesIO = (PreferencesIO) ((ObjectWrapperForBinder) extras.getBinder(preferencesIOExtras)).getData();
                preferencesStringKey = extras.getString(preferencesKeyExtras, null);
            }
            if (extras.containsKey(textHintExtras)) {
                textHint = extras.getString(textHintExtras, null);
            }
            if (extras.containsKey(textInputTypeExtras)) {
                inputType = extras.getInt(textInputTypeExtras, 0);
            }
            if (extras.containsKey(textMaxLengthExtras)) {
                maxLength = extras.getInt(textMaxLengthExtras, 0);
            }
        }

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button cancelButton = (Button) findViewById(R.id.cancel);
        final EditText editText = (EditText) findViewById(R.id.edit_comment);
        if (textHint != null) {
            editText.setHint(textHint);
        }
        if (inputType != 0) {
            editText.setInputType(inputType);
        }
        if (maxLength != 0) {
            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(editText.getText().toString().trim());
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveData(String data) {
        if (!data.equals("")) {
            if (jsonIO != null) {
                //TODO: Make it accept any class constructor
                jsonIO.writeToEndOfFile(new Comment(System.currentTimeMillis(), data).toJSONObject());
            }
            if (preferencesIO != null && preferencesStringKey != null) {
                preferencesIO.putString(preferencesStringKey, data);
            }
            Toast toast = Toast.makeText(getApplicationContext(),"Data is saved", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

package com.spectator.counter;

import android.content.Intent;
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
import com.spectator.utils.JsonIO;
import com.spectator.utils.ObjectWrapperForBinder;
import com.spectator.utils.PreferencesIO;

public class EditTextDialog extends BaseActivity {

    public static final String textHintExtras = "Hint for edit text";
    public static final String textInputTypeExtras = "Input type of EditText";
    public static final String textMaxLengthExtras = "Max length of entered text";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_dialog);

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
                Intent resultIntent = new Intent();
                resultIntent.putExtra("textResult", editText.getText().toString().trim());
                setResult(RESULT_OK, resultIntent);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.data_saved, Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}

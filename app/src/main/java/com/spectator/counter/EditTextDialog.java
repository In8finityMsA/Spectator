package com.spectator.counter;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spectator.R;
import com.spectator.data.Comment;
import com.spectator.utils.JsonIO;

public class EditTextDialog extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_dialog);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button cancelButton = (Button) findViewById(R.id.cancel);
        final EditText editText = (EditText) findViewById(R.id.edit_comment);
        final JsonIO commentJsonIO = new JsonIO(getFilesDir(), Comment.COMMENTS_PATH, Comment.ARRAY_KEY, JsonIO.MODE.WRITE_ONLY_EOF, false);

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim() != "")
                commentJsonIO.writeToEndOfFile(new Comment(System.currentTimeMillis(), editText.getText().toString().trim()).toJSONObject());
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
}

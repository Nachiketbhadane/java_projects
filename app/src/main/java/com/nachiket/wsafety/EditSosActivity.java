package com.nachiket.wsafety;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditSosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sos_message);

        EditText editText = findViewById(R.id.editTextSosMessage);
        Button saveBtn = findViewById(R.id.buttonSaveMessage);

        SharedPreferences prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // Load saved message
        editText.setText(prefs.getString("CUSTOM_MSG", "Help! I am in danger!"));

        saveBtn.setOnClickListener(v -> {
            String msg = editText.getText().toString().trim();

            if (!msg.isEmpty()) {
                prefs.edit().putString("CUSTOM_MSG", msg).apply();
                Toast.makeText(this, "SOS message updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

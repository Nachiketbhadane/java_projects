package com.nachiket.wsafety;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditSosActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveBtn;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sos_message);  // Make sure the XML file name is correct

        // Initialize the views
        editText = findViewById(R.id.editTextSosMessage);  // Use the correct ID as per your XML
        saveBtn = findViewById(R.id.buttonSaveMessage);    // Use the correct ID as per your XML

        // Initialize SharedPreferences
        prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // Get the current SOS message (default is "I'm in trouble!")
        String currentMessage = prefs.getString("CUSTOM_MSG", "I'm in trouble!");
        editText.setText(currentMessage);

        // Set the save button click listener
        saveBtn.setOnClickListener(v -> {
            String updatedMessage = editText.getText().toString().trim();

            if (!updatedMessage.isEmpty()) {
                // Save the updated message to SharedPreferences
                prefs.edit().putString("CUSTOM_MSG", updatedMessage).apply();
                Toast.makeText(this, "SOS message updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Message can't be empty!", Toast.LENGTH_SHORT).show();
            }

            finish(); // Close the activity
        });
    }
}

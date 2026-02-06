package com.nachiket.wsafety;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterNumberActivity extends AppCompatActivity {

    TextInputEditText number;
    ContactsDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_number);

        number = findViewById(R.id.add_contact);
        dbHelper = new ContactsDatabaseHelper(this);
    }

    public void saveNumber(View view) {
        String numberString = number.getText().toString().trim();
        if (numberString.length() == 10) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("number", numberString);
            long result = db.insert("emergency_contacts", null, values);
            db.close();

            if (result != -1) {
                Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving contact!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter a Valid Number!", Toast.LENGTH_SHORT).show();
        }
    }
}

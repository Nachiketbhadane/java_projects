package com.nachiket.wsafety;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RemoveContactActivity extends AppCompatActivity {
    LinearLayout layout;
    ContactsDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        dbHelper = new ContactsDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM emergency_contacts", null);
        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex("number"));
                Button btn = new Button(this);
                btn.setText(number);
                btn.setOnClickListener(v -> {
                    SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
                    writableDb.delete("emergency_contacts", "number=?", new String[]{number});
                    layout.removeView(btn);
                    Toast.makeText(this, "Contact removed!", Toast.LENGTH_SHORT).show();
                });
                layout.addView(btn);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}

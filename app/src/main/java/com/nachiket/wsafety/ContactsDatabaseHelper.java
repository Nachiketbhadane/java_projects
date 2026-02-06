package com.nachiket.wsafety;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contacts_db";

    // 🔴 IMPORTANT: increase version
    private static final int DB_VERSION = 2;

    public ContactsDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ✅ name + number
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS emergency_contacts (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "number TEXT UNIQUE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS emergency_contacts");
        onCreate(db);
    }

    // ✅ Fetch name + number
    public List<String> getAllEmergencyContacts() {

        List<String> contacts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name, number FROM emergency_contacts", null);

        if (cursor.moveToFirst()) {
            do {
                contacts.add(cursor.getString(0) + " : " + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public void deleteContact(String number) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("emergency_contacts", "number=?", new String[]{number});
        db.close();
    }
}

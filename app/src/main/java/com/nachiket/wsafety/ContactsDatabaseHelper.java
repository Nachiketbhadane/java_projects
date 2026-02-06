package com.nachiket.wsafety;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "contacts_db";
    private static final int DB_VERSION = 1;

    public ContactsDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS emergency_contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS emergency_contacts");
        onCreate(db);
    }

    // ✅ Fetch all contacts from the DB
    public List<String> getAllEmergencyContacts() {
        List<String> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT number FROM emergency_contacts", null);
        if (cursor.moveToFirst()) {
            do {
                contacts.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }

    // ✅ Delete contact by number
    public void deleteContact(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("emergency_contacts", "number = ?", new String[]{number});
        db.close();
    }
}

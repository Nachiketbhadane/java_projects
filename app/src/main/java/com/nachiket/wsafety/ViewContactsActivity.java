package com.nachiket.wsafety;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewContactsActivity extends AppCompatActivity {

    private ListView contactListView;
    private ContactsDatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private List<String> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);  // make sure this XML file exists

        contactListView = findViewById(R.id.view_contacts);  // ID in your XML
        dbHelper = new ContactsDatabaseHelper(this);

        loadContacts();

        contactListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedContact = contacts.get(position);

            new AlertDialog.Builder(ViewContactsActivity.this)
                    .setTitle("Remove Contact")
                    .setMessage("Are you sure you want to remove this contact?\n" + selectedContact)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteContact(selectedContact);
                        Toast.makeText(this, "Contact removed!", Toast.LENGTH_SHORT).show();
                        loadContacts();  // Refresh list
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadContacts() {
        contacts = dbHelper.getAllEmergencyContacts();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                contacts
        );

        contactListView.setAdapter(adapter);
    }
}

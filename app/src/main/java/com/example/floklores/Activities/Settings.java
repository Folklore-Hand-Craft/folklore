package com.example.floklores.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.floklores.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");


        // teams spinner
        Spinner sectionsList = findViewById(R.id.spinnerSection);
        String[] sections = new String[]{"","Team A", "Team B", "Team C"};
        ArrayAdapter<String> SectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sections);
        sectionsList.setAdapter(SectionsAdapter);

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();


        // save the user name in sharedPreference
        findViewById(R.id.imageViewSave).setOnClickListener(view -> {
            String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();


            Spinner sectionSpinner = (Spinner) findViewById(R.id.spinnerSection);
            String sectionName = sectionSpinner.getSelectedItem().toString();

            preferenceEditor.putString("username", username);
            preferenceEditor.putString("sectionName", sectionName);
            preferenceEditor.apply();

            Toast toast = Toast.makeText(this, "saved!", Toast.LENGTH_LONG);
            toast.show();
        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromSettingsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(Settings.this,MainActivity.class);
            startActivity(goHome);
        });
    }
}
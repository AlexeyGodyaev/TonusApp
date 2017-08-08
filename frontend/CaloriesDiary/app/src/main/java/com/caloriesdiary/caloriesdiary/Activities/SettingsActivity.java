package com.caloriesdiary.caloriesdiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.caloriesdiary.caloriesdiary.Fragments.SettingsFragment;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("НАСТРОЙКИ");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}
package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
<<<<<<< HEAD
import android.preference.Preference;
=======
>>>>>>> Alex's-branch
import android.support.v7.app.AppCompatActivity;



public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
<<<<<<< HEAD


=======
>>>>>>> Alex's-branch
    }
}

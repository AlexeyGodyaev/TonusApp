package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Fragments.SettingsFragment;
import com.caloriesdiary.caloriesdiary.R;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setTitle("НАСТРОЙКИ");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//
//        getFragmentManager().beginTransaction()
//                .replace(android.R.id.content, new SettingsFragment())
//                .commit();

    }

    public void onAccSetting(View view){
        Intent intent = new Intent(getApplicationContext(), AccSettingActivity.class);
        startActivity(intent);
    }

    public void onPushSetting(View view){
//        Intent intent = new Intent(this, );
//        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

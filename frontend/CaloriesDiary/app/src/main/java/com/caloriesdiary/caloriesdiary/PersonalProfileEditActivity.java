package com.caloriesdiary.caloriesdiary;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.ExecutionException;


public class PersonalProfileEditActivity extends AppCompatActivity{
    Button ok_btn;
    Spinner spinner;
    TextView name,age,height,weight,sleep,awake;
    RadioButton male,female;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    int DIALOG_TIME = 1;
    int myHour = 14;
    int myMinute = 35;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_edit_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ok_btn = (Button) findViewById(R.id.ok_btn);
        spinner = (Spinner) findViewById(R.id.activity_spinner);
        male = (RadioButton) findViewById(R.id.gender_male);
        female = (RadioButton) findViewById(R.id.gender_female);
        name = (EditText) findViewById(R.id.name_edit);
        age = (EditText) findViewById(R.id.age_edit);
        height = (EditText) findViewById(R.id.height_edit);
        weight = (EditText) findViewById(R.id.weight_edit);
        sleep = (EditText) findViewById(R.id.sleep_edit);
        awake = (TextView) findViewById(R.id.awake_edit);

        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();
       // InitPreference();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View view) throws InterruptedException, ExecutionException {
        String selected_activity = String.valueOf(spinner.getSelectedItemPosition());
        String selected_gender;
        if(male.isChecked())
        {
            selected_gender = "1";
        }
        else if ( female.isChecked())
        {
            selected_gender = "2";
        }
        else
        {
            selected_gender = "1";
        }
        Post log = new Post();

        String args [] = new String[10];

        args[0] = "http://192.168.1.205/users/save_user_chars";  //аргументы для пост запроса
        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
        args[2] = name.getText().toString();
        args[3] = weight.getText().toString();
        args[4] = height.getText().toString();
        args[5] = selected_gender;
        args[6] = age.getText().toString();
        args[7] = selected_activity;
        args[8] = sleep.getText().toString();
        args[9] = String.valueOf(myHour)+":"+String.valueOf(myMinute) + ":00";


        log.execute(args); // вызываем запрос

        //log.get().toString()
        Toast.makeText(getApplicationContext(), log.get().toString() ,Toast.LENGTH_LONG ).show();
        editor.putBoolean("IS_PROFILE_CREATED",true);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(),PersonalProfileActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    public void onAwakeTimeClick(View view)
    {
        showDialog(DIALOG_TIME);


    }
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            awake.setText("Среднее время пробуждения: "+ String.valueOf(myHour)+":"+String.valueOf(myMinute));
        }
    };

    public void InitPreference()
    {

        Map<String, ?> allEntries = sharedPref.getAll();   //Увидеть все настройки
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}

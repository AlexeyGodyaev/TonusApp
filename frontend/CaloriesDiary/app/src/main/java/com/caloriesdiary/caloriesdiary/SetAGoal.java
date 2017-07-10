package com.caloriesdiary.caloriesdiary;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class SetAGoal extends AppCompatActivity {
    EditText editGoalName, editWeight;
    TextView editDate;
    Spinner goalType, liveType;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    SeekBar periodsCount;
    Calendar calendar = Calendar.getInstance();
    String s[] = new String[7];
    Calendar today = Calendar.getInstance();
    int DIALOG_DATE = 1;
    int myYear;
    int myMonth;
    int myDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_a_goal_layout);

        goalType = (Spinner) findViewById(R.id.typeSpinner);
        liveType = (Spinner) findViewById(R.id.liveTypeSpinner);
        editGoalName = (EditText) findViewById(R.id.editGoal);
        editWeight = (EditText) findViewById(R.id.editWeight);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();
        periodsCount = (SeekBar) findViewById(R.id.periodsCountBar);
        periodsCount.setEnabled(false);

        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);


        today.set(Calendar.HOUR,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);

        editDate = (TextView) findViewById(R.id.goalDateText);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDay=calendar.get(Calendar.DAY_OF_MONTH);
                myMonth=calendar.get(Calendar.MONTH);
                myYear=calendar.get(Calendar.YEAR);


                showDialog(DIALOG_DATE);


            }
        });
        //periodsCount.setMax(5);

    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;

            calendar.set(Calendar.YEAR,myYear);
            calendar.set(Calendar.MONTH,myMonth);
            calendar.set(Calendar.DAY_OF_MONTH,myDay);

            Calendar endDay = calendar;
            long diff = endDay.getTimeInMillis()-today.getTimeInMillis();
            diff=diff/1000/60/60/24;
            s[3] = String.valueOf(diff);
            periodsCount.setEnabled(true);
            if (diff-1<10){
                periodsCount.setMax(Integer.parseInt(s[3])-1);
            }
            else{
                periodsCount.setMax(10);
            }

            editDate.setText(String.valueOf(myDay)+"/"+String.valueOf(myMonth)+"/"+String.valueOf(myYear));
        }
    };

    public void onStartRoadToGoal(View view){
        Post post = new Post();
        try {
        s[0] = "http://94.130.12.179/users/save_goal";
        s[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));

        s[2] = editWeight.getText().toString();
        s[4] = String.valueOf(liveType.getSelectedItemPosition()+1); s[5] = String.valueOf(goalType.getSelectedItemPosition()+1);
        s[6] = editGoalName.getText().toString();

            post.execute(s);

            Toast.makeText(getApplicationContext(), post.get().toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this, TodayActivity.class);
        startActivity(intent);
    }
}

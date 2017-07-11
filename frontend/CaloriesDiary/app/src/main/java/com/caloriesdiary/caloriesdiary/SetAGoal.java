package com.caloriesdiary.caloriesdiary;



import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class SetAGoal extends AppCompatActivity {
    EditText editGoalName, editWeight, editDate, editHrs, editTodayWeight;
    TextView  awake;
    Spinner goalType, liveType;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    SeekBar periodsCount;
    Calendar calendar = Calendar.getInstance();
    String s[] = new String[8];
    String s1 [] = new String[10];
    String awakestr = null;

    Calendar today = Calendar.getInstance();
    int DIALOG_TIME = 1;
    int myHour;
    int myMinute;
    int myDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_a_goal_layout);

        awake = (TextView) findViewById(R.id.awakeEditAgain);
        goalType = (Spinner) findViewById(R.id.typeSpinner);
        liveType = (Spinner) findViewById(R.id.liveTypeSpinner);
        editGoalName = (EditText) findViewById(R.id.editGoal);
        editWeight = (EditText) findViewById(R.id.editTodayWeight);
        editTodayWeight = (EditText) findViewById(R.id.editWeight);
        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
        editor = sharedPref.edit();
        periodsCount = (SeekBar) findViewById(R.id.periodsCountBar);
        periodsCount.setEnabled(false);
//-------------- данные пользователя для отправки
        s1[0] = "http://94.130.12.179/users/ave_user_chars";
        s1[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
        s1[2] = getIntent().getStringExtra("realNAme");
        s1[5] = getIntent().getStringExtra("sex");
        s1[6] = getIntent().getStringExtra("age");
        s1[4] = getIntent().getStringExtra("height");

//------------
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        editDate = (EditText) findViewById(R.id.editDaysCount);
        editHrs = (EditText) findViewById(R.id.editHrsCount);
        editDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                periodsCount.setEnabled(true);
                try {
                    if (editDate.getText().toString().length() > 0)
                        if (Integer.parseInt(editDate.getText().toString()) < 10) {
                            periodsCount.setMax(Integer.parseInt(editDate.getText().toString()) - 1);
                        } else periodsCount.setMax(9);
                    else periodsCount.setEnabled(false);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void onAwakeTimeClk(View view)
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

            if (myMinute <10) {
                awake.setText("Ср. время пробуждения: " + String.valueOf(myHour) + ":0" + String.valueOf(myMinute) + ":00");
                awakestr = String.valueOf(myHour) + ":0" + String.valueOf(myMinute) + ":00";
            }
            else {
                awake.setText("Ср. время пробуждения: " + String.valueOf(myHour) + ":" + String.valueOf(myMinute) + ":00");
                awakestr = String.valueOf(myHour) + ":" + String.valueOf(myMinute) + ":00";
            }
        }
    };


    public void onStartRoadToGoal(View view){
        Post post = new Post();
        try {
        s[0] = "http://94.130.12.179/users/save_goal";
        s[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
        s[7] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"."
                +String.valueOf(calendar.get(Calendar.MONTH)+1)+"."+String.valueOf(calendar.get(Calendar.YEAR));
        s[2] = editWeight.getText().toString();  s[3]= editDate.getText().toString();
        s[4] = String.valueOf(liveType.getSelectedItemPosition()+1); s[5] = String.valueOf(goalType.getSelectedItemPosition()+1);
        s[6] = editGoalName.getText().toString();

            post.execute(s);

            Toast.makeText(getApplicationContext(), post.get().toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        try {
            s1[7] = String.valueOf(liveType.getSelectedItemPosition()+1);
            s1[3] = editTodayWeight.getText().toString();
            s1[8] = editHrs.getText().toString();
            s1[9] = awakestr;

            post.execute(s1);
        } catch (Exception e){

        }
        Intent intent = new Intent(this, TodayActivity.class);
        startActivity(intent);
    }
}

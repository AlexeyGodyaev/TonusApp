package com.caloriesdiary.caloriesdiary;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
    SharedPreferences sharedPref;
    SeekBar periodsCount;
    final Calendar calendar = Calendar.getInstance();
    final String[] s = new String[8];
    final String[] s1  = new String[10];
    String awakestr = null;
    final String[]  saveGoal  = new String [20];

    final Calendar today = Calendar.getInstance();
    final int DIALOG_TIME = 1;
    int myHour;
    int myMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_a_goal_layout);

        awake = (TextView) findViewById(R.id.awakeEditAgain);
        goalType = (Spinner) findViewById(R.id.typeSpinner);
        liveType = (Spinner) findViewById(R.id.liveTypeSpinner);
        editGoalName = (EditText) findViewById(R.id.editGoal);
        editWeight = (EditText) findViewById(R.id.editWeight);
        editTodayWeight = (EditText) findViewById(R.id.editTodayWeight);
        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
        periodsCount = (SeekBar) findViewById(R.id.periodsCountBar);
        periodsCount.setEnabled(false);
//-------------- данные пользователя для отправки
        s1[0] = "http://caloriesdiary.ru/users/save_user_chars";
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
            return new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
        }
        return super.onCreateDialog(id);
    }

    final TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
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

        try {
            JSONObject jsn;
            JSONArray jArr;

            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();

                File food = new File(getCacheDir(), "Food.txt");
                File active = new File(getCacheDir(), "Actions.txt");
                food.delete();
                active.delete();
                f.delete();
                jsn = new JSONObject(text);
                jArr = jsn.getJSONArray("today_params");
                jsn.remove("today_params");


                Post getGoal = new Post();
                String getParams[] = new String[2];
                getParams[0] = "http://caloriesdiary.ru/users/get_goal";
                getParams[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));

                getGoal.execute(getParams);

                JSONObject js = getGoal.get();
                JSONObject jsonObject = js.getJSONObject("userGoal");


                saveGoal[0] = "http://caloriesdiary.ru/users/save_goal_archive";
                saveGoal[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                saveGoal[2] = jsonObject.getString("desired_weight");
                saveGoal[3] = jsonObject.getString("period");
                saveGoal[4] = jsonObject.getString("begin_date");
                saveGoal[5] = jsonObject.getString("goal");
                if(jArr.getJSONObject(jArr.length()-1).getString("mass").equals("")) {
                    saveGoal[6] = "0";
                }else
                {
                    saveGoal[6] = jArr.getJSONObject(jArr.length()-1).getString("mass");
                }
                saveGoal[7] = jsonObject.getString("activityType");
                saveGoal[9] = jsonObject.getString("name");
                if (jArr.length() < Integer.parseInt(jsonObject.getString("period"))) {
                    saveGoal[8] = "false";
                }else{ saveGoal[9] = "true";}
                saveGoal[10] = jArr.getJSONObject(0).getString("mass")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("mass");
                saveGoal[11] = jArr.getJSONObject(0).getString("lHand")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("lHand");
                saveGoal[12] =jArr.getJSONObject(0).getString("rHand")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("rHand");
                saveGoal[13] =jArr.getJSONObject(0).getString("chest")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("chest");
                saveGoal[14] =jArr.getJSONObject(0).getString("waist")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("waist");
                saveGoal[15] =jArr.getJSONObject(0).getString("butt")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("butt");
                saveGoal[16] =jArr.getJSONObject(0).getString("lLeg")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("lLeg");
                saveGoal[17] =jArr.getJSONObject(0).getString("rLeg")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("rLeg");
                saveGoal[18] =jArr.getJSONObject(0).getString("calves")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("calves");
                saveGoal[19] =jArr.getJSONObject(0).getString("shoulders")+"/"
                        +jArr.getJSONObject(jArr.length()-1).getString("shoulders");


                Post save = new Post();
                save.execute(saveGoal);
                JSONObject json = save.get();
                Toast.makeText(this, json.toString(), Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        Post post = new Post();
        try {
        s[0] = "http://caloriesdiary.ru/users/save_goal";
        s[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
        s[7] = String.valueOf(calendar.get(Calendar.YEAR))+"-"
                +String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
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
            Toast.makeText(this, "Сервис не доступен", Toast.LENGTH_SHORT).show();
        }
            Intent intent = new Intent(this, TodayActivity.class);
        startActivity(intent);
    }

}

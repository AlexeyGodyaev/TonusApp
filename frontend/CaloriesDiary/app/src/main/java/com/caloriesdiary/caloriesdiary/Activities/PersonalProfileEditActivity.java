package com.caloriesdiary.caloriesdiary.Activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Map;


public class PersonalProfileEditActivity extends AppCompatActivity implements CallBackListener{
    Spinner spinner;
    TextView name,age,height,weight,sleep,awake;
    RadioButton male,female;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String awakestr;
    private Toolbar mToolbar;

    int DIALOG_TIME = 1;
    int myHour = 14;
    int myMinute = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_edit_layout);

        spinner = (Spinner) findViewById(R.id.activity_spinner);
        male = (RadioButton) findViewById(R.id.gender_male);
        female = (RadioButton) findViewById(R.id.gender_female);
        name = (EditText) findViewById(R.id.name_edit);
        age = (EditText) findViewById(R.id.age_edit);
        height = (EditText) findViewById(R.id.height_edit);
        weight = (EditText) findViewById(R.id.weight_edit);
        sleep = (EditText) findViewById(R.id.sleep_edit);
        awake = (TextView) findViewById(R.id.awake_edit);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit_profile);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);

        try {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        initProfile();

       // InitPreference();
    }

    private void initProfile(){
        try
        {
            Post log = new Post();
            log.setListener(this);
            String args[] = new String[3];

            args[0] = "http://caloriesdiary.ru/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[2] = FirebaseInstanceId.getInstance().getToken();

            log.execute(args); // вызываем запрос
            JSONObject JSans = log.get();

            // Toast.makeText(getApplicationContext(),String.valueOf(sharedPref.getInt("PROFILE_ID",0)) + " " +JSans.toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(),JSans.toString(),Toast.LENGTH_LONG).show();

            if(JSans.getString("status").equals("1")) {
                name.setText(JSans.getJSONObject("userChars").getString("realName"));
                age.setText(JSans.getJSONObject("userChars").getString("age"));
                height.setText(JSans.getJSONObject("userChars").getString("height"));
                weight.setText(JSans.getJSONObject("userChars").getString("weight"));
                if (JSans.getJSONObject("userChars").getString("sex").equals("1")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
                switch (JSans.getJSONObject("userChars").getString("activityType")) {
                    case "1":
                        spinner.setSelection(0);
                        break;
                    case "2":
                        spinner.setSelection(1);
                        break;
                    case "3":
                        spinner.setSelection(2);
                        break;
                    case "4":
                        spinner.setSelection(3);
                        break;
                    case "5":
                        spinner.setSelection(4);
                        break;
                }
                sleep.setText(JSans.getJSONObject("userChars").getString("avgdream"));
                awake.setText("Ср. время пробуждения: " + JSans.getJSONObject("userChars").getString("wokeup"));
                awakestr = JSans.getJSONObject("userChars").getString("wokeup");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
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

    private boolean isEmpty(TextView text) {
        return (text.getText().toString().equals("") || text.getText().toString().length()==0);
    }

    public void onClick(View view)  {

        try {
            String selected_activity = String.valueOf(spinner.getSelectedItemPosition() + 1);
            String selected_gender;

            if (!isEmpty(name) && !isEmpty(weight) && !isEmpty(age) && !isEmpty(height)) {

                if (Integer.parseInt(weight.getText().toString()) > 10
                        && Integer.parseInt(weight.getText().toString()) < 750
                        && Integer.parseInt(age.getText().toString()) < 130
                        && Integer.parseInt(height.getText().toString()) < 300
                        && Integer.parseInt(sleep.getText().toString()) < 24) {

                    if (male.isChecked()) {
                        selected_gender = "1";
                    } else if (female.isChecked()) {
                        selected_gender = "2";
                    } else {
                        selected_gender = "1";
                    }

                    Post log = new Post();
                    log.setListener(this);
                    String args[] = new String[11];

                    args[0] = "http://caloriesdiary.ru/users/save_user_chars";  //аргументы для пост запроса
                    args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                    args[2] = name.getText().toString();
                    args[3] = weight.getText().toString();
                    args[4] = height.getText().toString();
                    args[5] = selected_gender;
                    args[6] = age.getText().toString();
                    args[7] = selected_activity;
                    args[8] = sleep.getText().toString();
                    args[9] = awakestr;
                    args[10] = FirebaseInstanceId.getInstance().getToken();

                    log.execute(args); // вызываем запрос

                    Toast.makeText(getApplicationContext(), log.get().toString(), Toast.LENGTH_LONG).show();
                    editor = sharedPref.edit();
                    editor.putBoolean("IS_PROFILE_CREATED", true);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Некоторые поля введены некорректно", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onAwakeTimeClick(View view)
    {
        showDialog(DIALOG_TIME);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            return  new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
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

    public void InitPreference()
    {

        Map<String, ?> allEntries = sharedPref.getAll();   //Увидеть все настройки
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    @Override
    public void callback() {

    }
}

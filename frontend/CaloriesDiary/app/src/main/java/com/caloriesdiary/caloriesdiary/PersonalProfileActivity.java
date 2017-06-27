package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;


public class PersonalProfileActivity extends AppCompatActivity{
    Button ok_btn;
    Spinner spinner;
    TextView name,age,height,weight;
    RadioButton male,female;

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
        name = (TextView) findViewById(R.id.name_edit);
        age = (TextView) findViewById(R.id.age_edit);
        height = (TextView) findViewById(R.id.height_edit);
        weight = (TextView) findViewById(R.id.weight_edit);


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
            selected_gender = "3";
        }
        Post log = new Post();

        String args [] = new String[7];

        args[0] = "http://192.168.1.205/users/auth";  //аргументы для пост запроса
        args[1] = name.getText().toString();
        args[2] = age.getText().toString();
        args[3] = selected_gender;
        args[4] = height.getText().toString();
        args[5] = weight.getText().toString();
        args[6] = selected_activity;

        log.execute(args); // вызываем запрос

        //log.get().toString()
        Toast.makeText(getApplicationContext(), log.get().toString() ,Toast.LENGTH_LONG ).show();
    }
}

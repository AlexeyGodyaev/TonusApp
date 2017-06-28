package com.caloriesdiary.caloriesdiary;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Date;

public class TodayActivity extends AppCompatActivity {

    TextView todayDate, dayOfTheWeek, countOfDays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);

        Date cur = new Date();

        todayDate = (TextView) findViewById(R.id.todayDate);
        todayDate.setText(cur.toString());
    }
}

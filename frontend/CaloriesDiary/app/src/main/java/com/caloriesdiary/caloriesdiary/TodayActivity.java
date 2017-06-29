package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.util.Calendar;

import java.util.Date;

public class TodayActivity extends AppCompatActivity {

    TextView todayDate, dayOfTheWeek, countOfDays, targetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);

        Calendar calendar = Calendar.getInstance();


        todayDate = (TextView) findViewById(R.id.todayDate);
        dayOfTheWeek = (TextView) findViewById(R.id.todayDayOfTheWeek);
        countOfDays = (TextView) findViewById(R.id.dayNumber);
        targetText = (TextView) findViewById(R.id.targetTextView);

        targetText.setText("Твой цель: -");
        todayDate.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))+"е "+getMonth(calendar.get(Calendar.MONTH)));
        dayOfTheWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
    }

    private String getDayOfWeek(int i){
        String s="";

        switch (i){
            case Calendar.SUNDAY:
                s = "Воскресенье";
                break;
            case Calendar.MONDAY:
                s = "Понедельник";
                break;
            case Calendar.TUESDAY:
                s = "Вторник";
                break;
            case Calendar.WEDNESDAY:
                s = "Среда";
                break;
            case Calendar.THURSDAY:
                s = "Четверг";
                break;
            case Calendar.FRIDAY:
                s = "Пятница";
                break;
            case Calendar.SATURDAY:
                s = "Суббота";
                break;
        }
        return s;
    }

    private  String getMonth(int i){
        String s="";
        switch (i){
            case Calendar.JANUARY:
                s = "Января";
                break;
            case Calendar.FEBRUARY:
                s = "Февраля";
                break;
            case Calendar.MARCH:
                s = "Марта";
                break;
            case Calendar.APRIL:
                s = "Апреля";
                break;
            case Calendar.MAY:
                s = "Мая";
                break;
            case Calendar.JUNE:
                s = "Июня";
                break;
            case Calendar.JULY:
                s = "Июля";
                break;
            case Calendar.AUGUST:
                s = "Августа";
                break;
            case Calendar.SEPTEMBER:
                s = "Сентября";
                break;
            case Calendar.OCTOBER:
                s = "Октября";
                break;
            case Calendar.NOVEMBER:
                s = "Ноября";
                break;
            case Calendar.DECEMBER:
                s = "Декабря";
                break;
        }

        return s;
    }
}

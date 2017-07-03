package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TodayActivity extends FragmentActivity {

    TextView todayDate, dayOfTheWeek, countOfDays, targetText;
    Button activityBtn, todayFoodBtn, addFoodBtn;
    ListView foodBasketList;
    FoodAdapter adapter = new FoodAdapter(this, initData());


    private boolean flag = true;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private FoodBasketFragment foodBasketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);

        manager = getSupportFragmentManager();
        foodBasketFragment = new FoodBasketFragment();
        foodBasketList = (ListView) findViewById(R.id.foodBasketList);
        //foodBasketList.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();

        activityBtn = (Button) findViewById(R.id.todayActiveBtn);
        todayFoodBtn = (Button) findViewById(R.id.todayFoodBtn);
        addFoodBtn = (Button) findViewById(R.id.addFoodBtn);

        todayDate = (TextView) findViewById(R.id.todayDate);
        dayOfTheWeek = (TextView) findViewById(R.id.todayDayOfTheWeek);
        countOfDays = (TextView) findViewById(R.id.dayNumber);
        targetText = (TextView) findViewById(R.id.targetTextView);

        targetText.setText("Твой цель: -");
        todayDate.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))+"е "+getMonth(calendar.get(Calendar.MONTH)));
        dayOfTheWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
    }

    public void onTodayFoodBtnClc(View v){
        transaction = manager.beginTransaction();

        if(flag == true){
            transaction.add(R.id.foodBasketFragment, foodBasketFragment); flag = false;}
        else {transaction.remove(foodBasketFragment);  flag = true;}

        transaction.commit();
    }

    public  void addFoodClc(View view) {
        Intent intent = new Intent(getApplicationContext(), FoodCatalogActivity.class);
        startActivity(intent);
    }

    public void onTodayActivityBtnClc(View v){
        Intent intent = new Intent(getApplicationContext(), ActionsCatalogActivity.class);
        startActivity(intent);
    }


    private List<FoodItem> initData() {
        List<FoodItem> list = new ArrayList<FoodItem>();
        String resp = null;
        String foodName;
        Float b=new Float(0), j=new Float(0), u=new Float(0), calories=new Float(0);
        Integer id=0;

        try {
            FileInputStream fin = null;
            fin = openFileInput("Food.txt");
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            resp = new String(bytes);
            Toast.makeText(getApplicationContext(), resp.substring(0,resp.indexOf('}')+1), Toast.LENGTH_SHORT).show();
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null)
            while(resp.length()>10){
                try {
                    JSONObject jOb = new JSONObject(resp.substring(0,resp.indexOf('}')+1));

                    try {
                        Integer f1 = new Integer(jOb.getString("id"));
                        id=f1;
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    }


                    foodName = jOb.getString("name");


                    String bJU = jOb.getString("bju");
                    try {
                        Float f1 = new Float(bJU.substring(0,bJU.indexOf('/')));
                        b = f1;
                        bJU=bJU.substring(bJU.indexOf('/')+1);
                        f1 = new Float(resp.substring(0,resp.indexOf('/')));
                        j=f1;
                        bJU=bJU.substring(bJU.indexOf('/')+1);
                        f1 = new Float(bJU);
                        u=f1;
                        f1 = new Float(jOb.getString("calories"));
                        calories=f1;
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    }

                    resp=resp.substring(resp.indexOf('}')+1);

                    if(b!=0||j!=0||u!=0||calories!=0)
                        list.add(new FoodItem(foodName,b,j,u,id,calories));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return list;
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

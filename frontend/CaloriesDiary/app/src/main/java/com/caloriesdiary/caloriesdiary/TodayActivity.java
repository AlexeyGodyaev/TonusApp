package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayActivity extends FragmentActivity {

    TextView todayDate, dayOfTheWeek, countOfDays, targetText;
    Button activityBtn, todayFoodBtn, addFoodBtn;
    public ListView foodBasketList;
    public FoodAdapter adapter = new FoodAdapter(getApplicationContext(), initData());


    private boolean flag = true;
    public FragmentManager manager;
    public FragmentTransaction transaction;
    public FoodBasketFragment foodBasketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);

        manager = getSupportFragmentManager();
        foodBasketFragment = new FoodBasketFragment();
        foodBasketList = (ListView) findViewById(R.id.foodBasketList);
        foodBasketList.setAdapter(adapter);

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


    public List<FoodItem> initData() {
            List<FoodItem> list = new ArrayList<FoodItem>();
            String resp = null;
            String foodName = null;
            Float b = new Float(0), j = new Float(0), u = new Float(0), calories = new Float(0);
            Integer id = 0;

            try {
                FileInputStream fin = null;
                fin = openFileInput("Food.txt");
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                resp = new String(bytes);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }

            if (resp != null) {
                try {
                    JSONObject jOb = new JSONObject(resp);
                    JSONArray jArr = jOb.getJSONArray("food");
                    for (int i = 0; i < jArr.length(); i++) {
                        try {
                            Integer i1 = new Integer(jArr.getJSONObject(i).getString("category_id"));
                            id = i1;
                            Float f1 = Float.parseFloat(jArr.getJSONObject(i).getString("protein"));
                            b = f1;
                            f1 = Float.parseFloat(jArr.getJSONObject(i).getString("fats"));
                            j = f1;
                            f1 = Float.parseFloat(jArr.getJSONObject(i).getString("carbs"));
                            u = f1;
                            f1 = Float.parseFloat(jArr.getJSONObject(i).getString("calories"));
                            calories = f1;

                        } catch (NumberFormatException e) {
                            System.err.println("Неверный формат строки!");
                        }

                        foodName = jArr.getJSONObject(i).getString("name");

                        if (foodName != null)
                            list.add(new FoodItem(foodName, b, j, u, id, calories));
                    }
                } catch (JSONException e) {
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

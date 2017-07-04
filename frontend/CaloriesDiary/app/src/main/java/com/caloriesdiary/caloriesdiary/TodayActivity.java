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
import java.util.concurrent.ExecutionException;

public class TodayActivity extends FragmentActivity {

    List<FoodItem> listFood = new ArrayList<FoodItem>();
    List<ActionItem> listActive = new ArrayList<ActionItem>();
    TextView todayDate, dayOfTheWeek, countOfDays, targetText;
    Button activityBtn, todayFoodBtn, addFoodBtn;
    ListView foodBasketList, activeBasketList;
    FoodAdapter adapter; //прихуярю сюда фрагмент чтоб блять можно было запустить обе листвьюхи
    ActionsAdapter actionsAdapter;

    private boolean foodFlag = false, activeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);

        adapter = new FoodAdapter(this, initFoodData());
        actionsAdapter = new ActionsAdapter(this, initActiveData());


        Calendar calendar = Calendar.getInstance();

        foodBasketList = (ListView) findViewById(R.id.my_food_basket);
        activeBasketList = (ListView) findViewById(R.id.my_active_basket);

        activityBtn = (Button) findViewById(R.id.todayActiveBtn);
        todayFoodBtn = (Button) findViewById(R.id.todayFoodBtn);
        //addFoodBtn = (Button) findViewById(R.id.addFoodBtn);

        todayDate = (TextView) findViewById(R.id.todayDate);
        dayOfTheWeek = (TextView) findViewById(R.id.todayDayOfTheWeek);
        countOfDays = (TextView) findViewById(R.id.dayNumber);
        targetText = (TextView) findViewById(R.id.targetTextView);

        targetText.setText("Твой цель: -");
        todayDate.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))+"е "+getMonth(calendar.get(Calendar.MONTH)));
        dayOfTheWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
    }



    public void onTodayFoodBtnClc(View v){
            initFoodData();
            foodBasketList.setAdapter(adapter);
    }

    public  void addFoodClc(View view) {
//        Intent intent = new Intent(getApplicationContext(), FoodCatalogActivity.class);
//        startActivity(intent);
    }

    public void onTodayActivityBtnClc(View v){
        initActiveData();
        activeBasketList.setAdapter(actionsAdapter);
//        Intent intent = new Intent(getApplicationContext(), ActionsCatalogActivity.class);
//        startActivity(intent);
    }


    private List<FoodItem> initFoodData() {
        String resp = null;
        String foodName = null;
        Float b=new Float(0), j=new Float(0), u=new Float(0), calories=new Float(0);
        Integer id=0;


        try {
            FileInputStream fin = null;
            fin = openFileInput("Food.txt");
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            resp = new String(bytes);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null&&foodFlag == true) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("food");
                for (int i = 0; i < 3; i++) {
//                    try {
//                        Integer i1 = new Integer(jArr.getJSONObject(i).getString("category_id"));
//                        id = i1;
//                        foodName = jArr.getJSONObject(i).getString("name");
//                        Float f1 = new Float(jArr.getJSONObject(i).getString("protein"));
//                        b = f1;
//                        f1 = new Float(jArr.getJSONObject(i).getString("fats"));
//                        j = f1;
//                        f1 = new Float(jArr.getJSONObject(i).getString("carbs"));
//                        u = f1;
//                        f1 = new Float(jArr.getJSONObject(i).getString("calories"));
//                        calories = f1;
//                    } catch (NumberFormatException e) {
//                        System.err.println("Неверный формат строки!");
//                    }
                    //if(b!=0||j!=0||u!=0||calories!=0)
                    listFood.add(new FoodItem("hui", 1f, 2f, 3f, 4, 1435f));
                    //list.add(new FoodItem(foodName,b,j,u,id,calories));
                }
            } catch (JSONException jEx) {
                Toast.makeText(getApplicationContext(), jEx.toString(), Toast.LENGTH_SHORT).show();
            }
            foodFlag = false;
        } else {
            while (listFood.isEmpty())
                listFood.remove(0);

            foodFlag = true;
        }
        return listFood;
    }

    private List<ActionItem> initActiveData() {
        String resp = null;
        String foodName = null;
        Float calories=new Float(0);
        Integer id=0;


        try {
            FileInputStream fin = null;
            fin = openFileInput("Food.txt");
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            resp = new String(bytes);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null&&activeFlag == true) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("food");
                for (int i = 0; i < 3; i++) {
//                    try {
//                        Integer i1 = new Integer(jArr.getJSONObject(i).getString("category_id"));
//                        id = i1;
//                        foodName = jArr.getJSONObject(i).getString("name");
//                        Float f1 = new Float(jArr.getJSONObject(i).getString("protein"));
//                        b = f1;
//                        f1 = new Float(jArr.getJSONObject(i).getString("fats"));
//                        j = f1;
//                        f1 = new Float(jArr.getJSONObject(i).getString("carbs"));
//                        u = f1;
//                        f1 = new Float(jArr.getJSONObject(i).getString("calories"));
//                        calories = f1;
//                    } catch (NumberFormatException e) {
//                        System.err.println("Неверный формат строки!");
//                    }
                    //if(b!=0||j!=0||u!=0||calories!=0)
                    listActive.add(new ActionItem("hui", 3f, 4));
                    //list.add(new FoodItem(foodName,b,j,u,id,calories));
                }
            } catch (JSONException jEx) {
                Toast.makeText(getApplicationContext(), jEx.toString(), Toast.LENGTH_SHORT).show();
            }
            activeFlag = false;
        } else {
            while (!listActive.isEmpty())
                listActive.remove(0);

            activeFlag = true;
        }
        return listActive;
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

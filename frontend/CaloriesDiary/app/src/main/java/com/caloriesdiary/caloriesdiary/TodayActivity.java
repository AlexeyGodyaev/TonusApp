package com.caloriesdiary.caloriesdiary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TodayActivity extends AppCompatActivity {

    List<FoodItem> list = new ArrayList<FoodItem>();
    List<ActionItem> listActive = new ArrayList<ActionItem>();

    TextView todayDate, dayOfTheWeek, countOfDays, targetText, todayFoodBtn , activityBtn, antropometry, calories;
    Button saveTodayParams;
    private TodayAntropometryFragment fragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private RecyclerView foodRecyclerView;
    private RecyclerView.Adapter foodAdapter;
    private RecyclerView.LayoutManager foodLayoutManager;

    private RecyclerView actionRecyclerView;
    private RecyclerView.Adapter actionAdapter;
    private RecyclerView.LayoutManager actionLayoutManager;

    private JSONArray todayParams = null;

    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    Calendar calendar;

    EditText editMass;
    private boolean foodFlag = false, activeFlag = false, antropometryFlag = true;

    public void CalcCalories()
    {
        try {
            File f = new File(getCacheDir(), "Food.txt");
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inObject = new ObjectInputStream(in);
            String text = inObject.readObject().toString();
            inObject.close();

            JSONObject jsn = new JSONObject(text);
            JSONArray jsonArray = jsn.getJSONArray("food");
            int sum = 0;

            for(int j=0; j < jsonArray.length();j++) {
                sum += Integer.parseInt(jsonArray.getJSONObject(j).get("calories").toString());
            }

            String answer = "Потреблено: " + sum + " ккал";

            f = new File(getCacheDir(), "Actions.txt");
            in = new FileInputStream(f);
            inObject = new ObjectInputStream(in);
            text = inObject.readObject().toString();
            inObject.close();

            jsn = new JSONObject(text);
            jsonArray = jsn.getJSONArray("active");
            sum = 0;

            for(int j=0; j < jsonArray.length();j++) {
                sum += Integer.parseInt(jsonArray.getJSONObject(j).get("calories").toString());
            }
                answer += " Сгорело: " + sum + " ккал";

            calories.setText(answer);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);
        setTitle("Сегодня");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        saveTodayParams = (Button) findViewById(R.id.save_today_params);

        antropometry = (TextView) findViewById(R.id.today_antropometry);
        foodRecyclerView = (RecyclerView) findViewById(R.id.food_busket_recycler_view);
        foodRecyclerView.setHasFixedSize(true);
        foodLayoutManager = new LinearLayoutManager(this);
        foodRecyclerView.setLayoutManager(foodLayoutManager);
        foodAdapter = new RecycleFoodAdapter(initFoodData());

        actionRecyclerView = (RecyclerView) findViewById(R.id.action_busket_recycler_view);
        actionRecyclerView.setHasFixedSize(true);
        actionLayoutManager = new LinearLayoutManager(this);
        actionRecyclerView.setLayoutManager(actionLayoutManager);
        actionAdapter = new RecycleActionAdapter(initActiveData());

        calendar = Calendar.getInstance();

        manager = getSupportFragmentManager();
        fragment = new TodayAntropometryFragment();

        editMass = (EditText) findViewById(R.id.edit_mass);

        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();

        activityBtn = (TextView) findViewById(R.id.todayActiveBtn);
        todayFoodBtn = (TextView) findViewById(R.id.todayFoodBtn);

        todayDate = (TextView) findViewById(R.id.todayDate);
        dayOfTheWeek = (TextView) findViewById(R.id.todayDayOfTheWeek);
        countOfDays = (TextView) findViewById(R.id.dayNumber);
        targetText = (TextView) findViewById(R.id.targetTextView);
        calories = (TextView) findViewById(R.id.foodCalories);

        CalcCalories();

        try {
            JSONObject jsn = new JSONObject();
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                todayParams = jsn.getJSONArray("today_params");
                jsn.remove("today_params");

                if (todayParams.length() > 0 && todayParams.getJSONObject(todayParams.length() - 1).getString("date")
                        .equals(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(calendar.get(Calendar.MONTH)) +
                                "." + String.valueOf(calendar.get(Calendar.YEAR)))){

                    editMass.setText(todayParams.getJSONObject(todayParams.length()-1).getString("mass"));
                }
            }
        } catch (Exception e){

        }
        actionRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                actionRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int i) {
                JSONObject jObject = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsn = new JSONObject();
                    File f = new File(getCacheDir(), "Actions.txt");
                    FileInputStream in = new FileInputStream(f);
                    ObjectInputStream inObject = new ObjectInputStream(in);
                    String text = inObject.readObject().toString();
                    inObject.close();

                    jsn = new JSONObject(text);
                    jsonArray = jsn.getJSONArray("active");
                    JSONArray jArray = new JSONArray();
                    jsn.remove("active");
                    for(int j=0; j<jsonArray.length();j++) {
                        if (j != i)
                            jArray.put(jsonArray.getJSONObject(j));
                    }
                    jObject.put("active", jArray);

                    FileOutputStream out = new FileOutputStream(f);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(jObject.toString());
                    outObject.flush();
                    out.getFD().sync();
                    outObject.close();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"1 +" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                listActive.remove(i);
                actionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        foodRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                foodRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int i) {
                JSONObject jObject = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsn = new JSONObject();
                    File f = new File(getCacheDir(), "Food.txt");
                    FileInputStream in = new FileInputStream(f);
                    ObjectInputStream inObject = new ObjectInputStream(in);
                    String text = inObject.readObject().toString();
                    inObject.close();

                    jsn = new JSONObject(text);
                    jsonArray = jsn.getJSONArray("food");
                    JSONArray jArray = new JSONArray();
                    jsn.remove("food");
                    for(int j=0; j<jsonArray.length();j++) {
                        if (j != i)
                            jArray.put(jsonArray.getJSONObject(j));
                    }
                    jObject.put("food", jArray);

                    FileOutputStream out = new FileOutputStream(f);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(jObject.toString());
                    outObject.flush();
                    out.getFD().sync();
                    outObject.close();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"1 +" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                list.remove(i);
                foodAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        try {
        Post post = new Post();
        String s [] = new String[2];
        s[0] = "http://94.130.12.179/users/get_goal"; s[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));

        post.execute(s);

            JSONObject js = post.get();
            JSONObject jo = js.getJSONObject("userGoal");
            targetText.setText("Твоя цель: "+ jo.getString("name"));

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        todayDate.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))+"е "+getMonth(calendar.get(Calendar.MONTH)));
        dayOfTheWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));


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
    public void onTodayFoodBtnClc(View v){
        initFoodData();

        foodRecyclerView.setAdapter(foodAdapter);
    }

    public void todayAntrClc(View view) {
        transaction = manager.beginTransaction();
        try {
            if (antropometryFlag == true) {
                transaction.add(R.id.antropometry_today, fragment);
                antropometryFlag = false;


            } else {
                transaction.remove(fragment);
                antropometryFlag = true;
            }

            transaction.commit();

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void onTodayActivityBtnClc(View v){
        initActiveData();
        actionRecyclerView.setAdapter(actionAdapter);
        onContentChanged();

    }


    private List<FoodItem> initFoodData() {
        String resp = null;
        String foodName = null;
        Float b=new Float(0), j=new Float(0), u=new Float(0), calories=new Float(0);
        Integer id=0;


        try {
            File f = new File(getCacheDir(), "Food.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                resp = inObject.readObject().toString();
                inObject.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"2 +" +  ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null&&foodFlag == true) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("food");
                for (int i = 0; i < jArr.length(); i++) {
                    try {
                        foodName = jArr.getJSONObject(i).getString("name");
                        Float f1 = new Float(jArr.getJSONObject(i).getString("protein"));
                        b = f1;
                        f1 = new Float(jArr.getJSONObject(i).getString("fats"));
                        j = f1;
                        f1 = new Float(jArr.getJSONObject(i).getString("carbs"));
                        u = f1;
                        f1 = new Float(jArr.getJSONObject(i).getString("calories"));
                        calories = f1;
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    }
                    if(b!=0||j!=0||u!=0||calories!=0)
                        list.add(new FoodItem(foodName,b,j,u,id,calories));
                }
            } catch (JSONException jEx) {
                Toast.makeText(getApplicationContext(),"3 +" +  jEx.toString(), Toast.LENGTH_SHORT).show();
            }
            foodFlag = false;
        } else {
            while (!list.isEmpty())
                list.remove(0);

            foodFlag = true;
        }
        return list;
    }

    public void onSaveTodayDataClc (View view){
        try {
            JSONObject jObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsn = new JSONObject();
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                jsonArray = jsn.getJSONArray("today_params");
                jsn.remove("today_params");
            }

            jsn.put("mass", editMass.getText().toString());

            jsn.put("date", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(calendar.get(Calendar.MONTH)) +
                    "." + String.valueOf(calendar.get(Calendar.YEAR)));
            if (fragment.getView() != null) {
                jsn.put("rLeg", fragment.getrLeg().getText().toString());
                jsn.put("lLeg", fragment.getlLeg().getText().toString());
                jsn.put("rHand", fragment.getrHand().getText().toString());
                jsn.put("lHand", fragment.getlHand().getText().toString());
                jsn.put("calves", fragment.getCalves().getText().toString());
                jsn.put("shoulders", fragment.getShoulders().getText().toString());
                jsn.put("butt", fragment.getButt().getText().toString());
                jsn.put("waist", fragment.getWaist().getText().toString());
                jsn.put("chest", fragment.getChest().getText().toString());
            }
            else
            {
                if (jsonArray.length() > 0 && jsonArray.getJSONObject(jsonArray.length() - 1).getString("date")
                        .equals(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(calendar.get(Calendar.MONTH)) +
                                "." + String.valueOf(calendar.get(Calendar.YEAR)))) {
                    jsn.put("rLeg", jsonArray.getJSONObject(jsonArray.length() - 1).getString("rLeg"));
                    jsn.put("lLeg", jsonArray.getJSONObject(jsonArray.length() - 1).getString("lLeg"));
                    jsn.put("rHand", jsonArray.getJSONObject(jsonArray.length() - 1).getString("rHand"));
                    jsn.put("lHand", jsonArray.getJSONObject(jsonArray.length() - 1).getString("lHand"));
                    jsn.put("calves", jsonArray.getJSONObject(jsonArray.length() - 1).getString("calves"));
                    jsn.put("shoulders", jsonArray.getJSONObject(jsonArray.length() - 1).getString("shoulders"));
                    jsn.put("butt", jsonArray.getJSONObject(jsonArray.length() - 1).getString("butt"));
                    jsn.put("waist", jsonArray.getJSONObject(jsonArray.length() - 1).getString("waist"));
                    jsn.put("chest", jsonArray.getJSONObject(jsonArray.length() - 1).getString("chest"));
                }
                else {
                    jsn.put("rLeg", "");
                    jsn.put("lLeg", "");
                    jsn.put("rHand", "");
                    jsn.put("lHand", "");
                    jsn.put("calves", "");
                    jsn.put("shoulders", "");
                    jsn.put("butt", "");
                    jsn.put("waist", "");
                    jsn.put("chest", "");
                }
            }
            if (jsonArray.length() > 0 && jsonArray.getJSONObject(jsonArray.length() - 1).getString("date")
                    .equals(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(calendar.get(Calendar.MONTH)) +
                            "." + String.valueOf(calendar.get(Calendar.YEAR)))) {
                JSONArray jArray = new JSONArray();
                for (int i = 0; i < jsonArray.length() - 1; i++)
                    jArray.put(jsonArray.getJSONObject(i));
                jArray.put(jsn);
                jObject.put("today_params", jArray);
            } else {
                jsonArray.put(jsn);
                jObject.put("today_params", jsonArray);
            }

            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream outObject = new ObjectOutputStream(out);
            outObject.writeObject(jObject.toString());
            outObject.flush();
            out.getFD().sync();
            outObject.close();

        } catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private List<ActionItem> initActiveData() {
        String resp = null;
        String actionName = null;
        Float calories=new Float(0);
        Integer id=0;


        try {
            File f = new File(getCacheDir(), "Actions.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                resp = inObject.readObject().toString();
                inObject.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null&&activeFlag == true) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("active");
                for (int i = 0; i < jArr.length(); i++) {
                    try {
                        actionName = jArr.getJSONObject(i).getString("name");
                        Float f1 = new Float(jArr.getJSONObject(i).getString("calories"));
                        calories = f1;
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    }
                    if(calories!=0)

                    listActive.add(new ActionItem(actionName, calories, 5));
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

    private String getMonth(int i){
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
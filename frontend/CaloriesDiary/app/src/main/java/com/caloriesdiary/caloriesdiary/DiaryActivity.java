package com.caloriesdiary.caloriesdiary;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class DiaryActivity extends AppCompatActivity {

    final List<FoodItem> list = new ArrayList<>();
    final List<ActionItem> listActive = new ArrayList<>();

    int sum, sum1;

    TextView todayDate, dayNote,  foodCalories, sportCalories, normCalories, carbs, fats, protein;
    private TodayAntropometryFragment fragment;
    private FragmentManager manager;
    FragmentTransaction transaction;

    private RecyclerView foodRecyclerView;
    private RecyclerView.Adapter foodAdapter;
    RecyclerView.LayoutManager foodLayoutManager;

    private RecyclerView actionRecyclerView;
    private RecyclerView.Adapter actionAdapter;
    RecyclerView.LayoutManager actionLayoutManager;

    JSONArray todayParams;

    SharedPreferences sharedPref;
    Calendar calendar;
    EditText editMass;
    private boolean foodFlag = false, activeFlag = false, antropometryFlag = true, FABFlag=false;

    final String[] args = new String[2];
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.diary_layout);
        setTitle("Дневник");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        foodRecyclerView = (RecyclerView) findViewById(R.id.diary_food_busket_recycler_view);
        foodRecyclerView.setHasFixedSize(true);
        foodLayoutManager = new LinearLayoutManager(this);
        foodRecyclerView.setLayoutManager(foodLayoutManager);
        foodAdapter = new RecycleFoodAdapter(initFoodData());

        actionRecyclerView = (RecyclerView) findViewById(R.id.diary_action_busket_recycler_view);
        actionRecyclerView.setHasFixedSize(true);
        actionLayoutManager = new LinearLayoutManager(this);
        actionRecyclerView.setLayoutManager(actionLayoutManager);
        actionAdapter = new RecycleActionAdapter(initActiveData());

        calendar = Calendar.getInstance();

        manager = getSupportFragmentManager();
        fragment = new TodayAntropometryFragment();

        editMass = (EditText) findViewById(R.id.diary_mass);
        dayNote = (TextView) findViewById(R.id.diaryDayNote);

        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);


        args[0] = "http://caloriesdiary.ru/calories/get_per_day";
        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));


        todayDate = (TextView) findViewById(R.id.diaryDate);
        //targetText = (TextView) findViewById(R.id.targetTextView);
        foodCalories = (TextView) findViewById(R.id.diaryFoodCalories);
        sportCalories = (TextView) findViewById(R.id.diarySportCalories);
        normCalories = (TextView) findViewById(R.id.diaryCaloriesNormText);


        try {
            JSONObject jsn;
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
                                "." + String.valueOf(calendar.get(Calendar.YEAR)))) {

                    editMass.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("mass"));
                    dayNote.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("note"));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        actionRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                actionRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int i) {
                JSONObject jObject = new JSONObject();
                try {
                    JSONArray jsonArray;
                    JSONObject jsn;
                    File f = new File(getCacheDir(), "Actions.txt");
                    FileInputStream in = new FileInputStream(f);
                    ObjectInputStream inObject = new ObjectInputStream(in);
                    String text = inObject.readObject().toString();
                    inObject.close();

                    jsn = new JSONObject(text);
                    jsonArray = jsn.getJSONArray("active");
                    JSONArray jArray = new JSONArray();
                    jsn.remove("active");
                    for (int j = 0; j < jsonArray.length(); j++) {
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

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "1 +" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                listActive.remove(i);
                actionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long press on position :" + position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        foodRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                foodRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int i) {
                JSONObject jObject = new JSONObject();
                try {
                    JSONArray jsonArray;
                    JSONObject jsn;
                    File f = new File(getCacheDir(), "Food.txt");
                    FileInputStream in = new FileInputStream(f);
                    ObjectInputStream inObject = new ObjectInputStream(in);
                    String text = inObject.readObject().toString();
                    inObject.close();

                    jsn = new JSONObject(text);
                    jsonArray = jsn.getJSONArray("food");
                    JSONArray jArray = new JSONArray();
                    jsn.remove("food");
                    for (int j = 0; j < jsonArray.length(); j++) {
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

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "1 +" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                list.remove(i);
                foodAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long press on position :" + position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        // try {
        // Post post = new Post();
        //String s[] = new String[2];
        //s[0] = "http://caloriesdiary.ru/users/get_goal";
        //s[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));

        //post.execute(s);

//            JSONObject js = post.get();
        //          JSONObject jo = js.getJSONObject("userGoal");
        //        targetText.setText("Твоя цель: " + jo.getString("name"));

        //  } catch (Exception e) {
        //    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        //}

        todayDate.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "." + getMonth(calendar.get(Calendar.MONTH)) + "." + calendar.get(Calendar.YEAR));

    }


    @Override
    protected void onResume() {
        super.onResume();

        Post log = new Post();
        log.execute(args);

        try {

            JSONObject resp = log.get();
            normCalories.setText(resp.getInt("result") + " ккал");

            protein.setText(resp.getInt("protein") + " г.");
            fats.setText(resp.getInt("fats")+ " г.");
            carbs.setText(resp.getInt("carbs")+ " г.");

            File f = new File(getCacheDir(), "Food.txt");
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inObject = new ObjectInputStream(in);
            String text = inObject.readObject().toString();
            inObject.close();

            JSONObject jsn = new JSONObject(text);
            JSONArray jsonArray = jsn.getJSONArray("food");

            for (int j = 0; j < jsonArray.length(); j++) {
                sum += Integer.parseInt(jsonArray.getJSONObject(j).get("calories").toString());
            }

            foodCalories.setText(sum + " ккал");


            f = new File(getCacheDir(), "Actions.txt");
            in = new FileInputStream(f);
            inObject = new ObjectInputStream(in);
            text = inObject.readObject().toString();
            inObject.close();

            jsn = new JSONObject(text);
            jsonArray = jsn.getJSONArray("active");


            for (int j = 0; j < jsonArray.length(); j++) {
                sum1 += Integer.parseInt(jsonArray.getJSONObject(j).get("calories").toString());
            }

            sportCalories.setText(sum1 + " ккал");

        }
        catch (Exception e)
        {
            if(foodCalories.getText() != "")
            {
                foodCalories.setText(sum + " ккал");
            }
            else
            {
                foodCalories.setText("");
            }

            if(sportCalories.getText() != "")
            {
                sportCalories.setText(sum1 + " ккал");
            }
            else
            {
                foodCalories.setText("");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDiaryFoodBtnClc(View v) {
        initFoodData();

        foodRecyclerView.setAdapter(foodAdapter);
    }

    public void diaryAntrClc(View view) {
        transaction = manager.beginTransaction();
        try {
            if (antropometryFlag) {
                transaction.add(R.id.antropometry_diary, fragment);
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



    public void onDiaryActivityBtnClc(View v) {
        initActiveData();
        actionRecyclerView.setAdapter(actionAdapter);
        onContentChanged();

    }

    private List<FoodItem> initFoodData() {
        String resp = null;
        String foodName = null;
        Float b = 0.0f, j = 0.0f, u = 0.0f, calories = 0.0f;
        Integer id = 0, category_id = 0;


        try {
            File f = new File(getCacheDir(), "Food.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                resp = inObject.readObject().toString();
                inObject.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "2 +" + ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null && foodFlag) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("food");
                for (int i = 0; i < jArr.length(); i++) {
                    try {
                        foodName = jArr.getJSONObject(i).getString("name");
                        b = Float.valueOf(jArr.getJSONObject(i).getString("protein"));
                        j = Float.valueOf(jArr.getJSONObject(i).getString("fats"));
                        u = Float.valueOf(jArr.getJSONObject(i).getString("carbs"));
                        calories = Float.valueOf(jArr.getJSONObject(i).getString("calories"));
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    }
                    list.add(new FoodItem(id, foodName, b, j, u, category_id, calories));
                }
            } catch (JSONException jEx) {
                Toast.makeText(getApplicationContext(), "3 +" + jEx.toString(), Toast.LENGTH_SHORT).show();
            }
            foodFlag = false;
        } else {
            while (!list.isEmpty())
                list.remove(0);

            foodFlag = true;
        }
        return list;
    }

    private List<ActionItem> initActiveData() {
        String resp = null;
        String actionName = null;
        Float calories = 0.0f;


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

        if (resp != null && activeFlag) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("active");
                for (int i = 0; i < jArr.length(); i++) {
                    try {
                        actionName = jArr.getJSONObject(i).getString("name");
                        calories = Float.valueOf(jArr.getJSONObject(i).getString("calories"));
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    }
                    if (calories != 0)

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


    private String getMonth(int i) {
        String s = "";
        switch (i) {
            case Calendar.JANUARY:
                s = "01";
                break;
            case Calendar.FEBRUARY:
                s = "02";
                break;
            case Calendar.MARCH:
                s = "03";
                break;
            case Calendar.APRIL:
                s = "04";
                break;
            case Calendar.MAY:
                s = "05";
                break;
            case Calendar.JUNE:
                s = "06";
                break;
            case Calendar.JULY:
                s = "07";
                break;
            case Calendar.AUGUST:
                s = "08";
                break;
            case Calendar.SEPTEMBER:
                s = "09";
                break;
            case Calendar.OCTOBER:
                s = "10";
                break;
            case Calendar.NOVEMBER:
                s = "11";
                break;
            case Calendar.DECEMBER:
                s = "12";
                break;
        }

        return s;
    }
}


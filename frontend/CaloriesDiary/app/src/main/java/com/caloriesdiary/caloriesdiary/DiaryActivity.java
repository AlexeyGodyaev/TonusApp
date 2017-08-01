package com.caloriesdiary.caloriesdiary;



import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    final List<FoodItem> list = new ArrayList<>();
    final List<ActionItem> listActive = new ArrayList<>();

    int position;

    TextView todayDate, dayNote,  foodCalories, sportCalories, normCalories;
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
    private boolean foodFlag = false, activeFlag = false, antropometryFlag = true;

    Calendar minDate = Calendar.getInstance(), maxDate = Calendar.getInstance();
    final String[] args = new String[2];

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

        todayDate = (TextView) findViewById(R.id.diaryDate);
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

                position = todayParams.length() - 1;

                if (todayParams.length() > 0) {

                    editMass.setText(todayParams.getJSONObject(position).getString("mass"));
                    dayNote.setText(todayParams.getJSONObject(position).getString("note"));

                    String s;
                    int day, month, year;
                    s = todayParams.getJSONObject(0).getString("date");

                    day = Integer.valueOf(s.substring(0, s.indexOf('.')));
                    s = s.substring(s.indexOf('.') + 1);

                    month = Integer.valueOf(s.substring(0, s.indexOf('.')));
                    s = s.substring(s.indexOf('.') + 1);

                    year = Integer.valueOf(s);
                    minDate.set(year, month - 1, day);

                    s = todayParams.getJSONObject(todayParams.length() - 1).getString("date");

                    day = Integer.valueOf(s.substring(0, s.indexOf('.')));
                    s = s.substring(s.indexOf('.') + 1);

                    month = Integer.valueOf(s.substring(0, s.indexOf('.')));
                    s = s.substring(s.indexOf('.') + 1);

                    year = Integer.valueOf(s);
                    maxDate.set(year, month - 1, day);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        todayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(getApplicationContext());
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                        datePicker.setMaxDate(maxDate.getTimeInMillis());
//                        datePicker.setMinDate(minDate.getTimeInMillis());

                    }
                });
                dpd.show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            JSONObject jsn = todayParams.getJSONObject(position);

            todayDate.setText(jsn.getString("date"));
            foodCalories.setText(jsn.getString("food_sum") + " ккал");

            sportCalories.setText(jsn.getString("active_sum") + " ккал");
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
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
        String foodName = null;
        Float b = 0.0f, j = 0.0f, u = 0.0f, calories = 0.0f;
        Integer id = 0, category_id = 0;

        if (foodFlag) {
            try {
                JSONObject jOb = todayParams.getJSONObject(position);
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
        String actionName = null;
        Float calories = 0.0f;

        if (activeFlag) {
            try {
                JSONObject jOb = todayParams.getJSONObject(position);
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
}


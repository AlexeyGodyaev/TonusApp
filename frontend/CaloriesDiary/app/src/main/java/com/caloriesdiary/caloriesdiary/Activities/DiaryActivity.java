package com.caloriesdiary.caloriesdiary.Activities;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.Items.ActionItem;
import com.caloriesdiary.caloriesdiary.Posts.GetDays;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.Adapters.RecycleActionAdapter;
import com.caloriesdiary.caloriesdiary.Adapters.RecycleFoodAdapter;
import com.caloriesdiary.caloriesdiary.Fragments.TodayAntropometryFragment;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

    TextView todayDate, dayNote,  foodCalories, sportCalories, normCalories, protein, carbs, fats;
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
    private boolean foodFlag = false, activeFlag = false, antropometryFlag = true, dateFlag = false;

    Calendar minDate = Calendar.getInstance(), maxDate = Calendar.getInstance();
    final String[] args = new String[2];
    final int dialogId = 1;
    int dialogDay, dialogMonth, dialogYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.diary_layout);
        setTitle("Дневник");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        protein = (TextView) findViewById(R.id.ProteinText);
        fats = (TextView) findViewById(R.id.FatsText);
        carbs = (TextView) findViewById(R.id.CarbsText);

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

        initDiaryData();

        todayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(dialogId);
            }
        });
    }

    private void initDiaryData(){
        try {
            JSONObject jsn;
            GetDays getDays = new GetDays();

            String args[] = new String[2];

            args[0] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
            args[1] = FirebaseInstanceId.getInstance().getToken();
            getDays.execute(args);
            //Toast.makeText(this, getDays.get(), Toast.LENGTH_LONG).show();
            jsn = new JSONObject(getDays.get());
            todayParams = jsn.getJSONArray("days");
            jsn.remove("days");

            position = todayParams.length()-1;
            if (todayParams.length() > 0) {

                editMass.setText(todayParams.getJSONObject(position).getString("mass"));
                dayNote.setText(todayParams.getJSONObject(position).getString("note"));
                todayDate.setText(todayParams.getJSONObject(position).getString("date"));
                foodCalories.setText(todayParams.getJSONObject(position).getString("food_sum") + " ккал");
                sportCalories.setText(todayParams.getJSONObject(position).getString("active_sum") + " ккал");
                protein.setText(todayParams.getJSONObject(position).getString("protein"));
                fats.setText(todayParams.getJSONObject(position).getString("fats"));
                carbs.setText(todayParams.getJSONObject(position).getString("carbs"));
                String s;
                int day, month, year;
                s = todayParams.getJSONObject(0).getString("date");

                year = Integer.valueOf(s.substring(0, s.indexOf('-')));
                s = s.substring(s.indexOf('-') + 1);

                month = Integer.valueOf(s.substring(0, s.indexOf('-')));
                s = s.substring(s.indexOf('-') + 1);

                day = Integer.valueOf(s);
                minDate.set(year, month - 1, day);

                s = todayParams.getJSONObject(todayParams.length() - 1).getString("date");

                year = Integer.valueOf(s.substring(0, s.indexOf('-')));
                s = s.substring(s.indexOf('-') + 1);

                month = Integer.valueOf(s.substring(0, s.indexOf('-')));
                s = s.substring(s.indexOf('-') + 1);

                day = Integer.valueOf(s);
                dialogDay = day;  dialogMonth = month-1; dialogYear = year;
                maxDate.set(year, month - 1, day);
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case dialogId:
                DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener,
                        dialogYear, dialogMonth, dialogDay);
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                //Настраиваем на текущую дату:
                return dialog;
        }
        return null;
    }
    //Делаем выбор желаемой даты:
    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
            try {
                String date = String.valueOf(selectedYear);
                if (selectedMonth<9)
                    date+="-0"+String.valueOf(selectedMonth+1); else date+=String.valueOf(selectedMonth+1);
                if (selectedDay<10)
                    date+="-0"+String.valueOf(selectedDay); else date+=String.valueOf(selectedDay);

                for (int i = 0; i < todayParams.length(); i++) {
                    if (todayParams.getJSONObject(i).getString("date")
                            .equals(date))
                        position = i;
                    Toast.makeText(DiaryActivity.this, date+"..."+todayParams.getJSONObject(i).getString("date"), Toast.LENGTH_LONG).show();
                }

                onChangeDiaryData();
                dateFlag = !dateFlag;
                initActiveData();
                initFoodData();
                actionRecyclerView.setAdapter(actionAdapter);
                foodRecyclerView.setAdapter(foodAdapter);
                dateFlag = !dateFlag;

            }catch (Exception e){
                Toast.makeText(DiaryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    protected void onChangeDiaryData() {
        try {
            JSONObject jsn = todayParams.getJSONObject(position);

            editMass.setText(jsn.getString("mass"));
            dayNote.setText(jsn.getString("note"));
            todayDate.setText(jsn.getString("date"));
            foodCalories.setText(jsn.getString("food_sum") + " ккал");
            sportCalories.setText(jsn.getString("active_sum") + " ккал");
            protein.setText(jsn.getString("protein"));
            fats.setText(jsn.getString("fats"));
            carbs.setText(jsn.getString("carbs"));
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
    }

    private List<FoodItem> initFoodData() {
        String foodName = null;
        Float b = 0.0f, j = 0.0f, u = 0.0f, calories = 0.0f;
        Integer id = 0, category_id = 0;

        if(dateFlag){
            while (!list.isEmpty())
                list.remove(0);
            foodFlag =!foodFlag;
        }

        if (foodFlag) {

            try {
                JSONObject jOb = todayParams.getJSONObject(position);
                JSONArray jArr = new JSONArray(jOb.getString("food"));
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
        if(dateFlag){
            while (!listActive.isEmpty())
                listActive.remove(0);
            activeFlag = !activeFlag;
        }

        if (activeFlag) {

            try {
                JSONObject jOb = todayParams.getJSONObject(position);
                JSONArray jArr = new JSONArray(jOb.getString("active"));
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



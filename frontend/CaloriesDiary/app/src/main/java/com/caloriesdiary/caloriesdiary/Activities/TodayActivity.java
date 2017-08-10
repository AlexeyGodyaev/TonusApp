package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Adapters.RecycleActionAdapter;
import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.Items.ActionItem;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.HTTP.SaveTodayParams;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.Adapters.RecycleFoodAdapter;
import com.caloriesdiary.caloriesdiary.RecyclerTouchListener;
import com.google.firebase.iid.FirebaseInstanceId;

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

    final List<FoodItem> list = new ArrayList<>();
    final List<ActionItem> listActive = new ArrayList<>();

    int sum, sum1;

    LinearLayout antropometry;
    NestedScrollView scrlView;

    private EditText rLeg, lLeg, rHand, lHand, chest, waist, butt, calves, shoulders;
    TextView todayDate, foodCalories, sportCalories, normCalories, carbs, fats, protein;
    EditText dayNote;

    private RecyclerView foodRecyclerView;
    private RecyclerView.Adapter foodAdapter;
    RecyclerView.LayoutManager foodLayoutManager;

    private RecyclerView actionRecyclerView;
    private RecyclerView.Adapter actionAdapter;
    RecyclerView.LayoutManager actionLayoutManager;

    SharedPreferences sharedPref;
    Calendar calendar;
    EditText editMass;
    private boolean foodFlag = false, activeFlag = false, antropometryFlag = true, FABFlag=false;

    final String[] args = new String[3];
    LinearLayout linearLayout;

    JSONArray jsonAction, jsonFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.today_layout);
        setTitle("Сегодня");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addViews();

        foodRecyclerView.setHasFixedSize(true);
        foodLayoutManager = new LinearLayoutManager(this);
        foodRecyclerView.setLayoutManager(foodLayoutManager);
        foodAdapter = new RecycleFoodAdapter(initFoodData());

        actionRecyclerView.setHasFixedSize(true);
        actionLayoutManager = new LinearLayoutManager(this);
        actionRecyclerView.setLayoutManager(actionLayoutManager);
        actionAdapter = new RecycleActionAdapter(initActiveData());

        calendar = Calendar.getInstance();
        antropometry.setVisibility(View.GONE);

        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);

        linearLayout.setVisibility(View.INVISIBLE);

        try {
            JSONObject jsn;
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();

                jsn = new JSONObject(text);

                String date = String.valueOf(calendar.get(Calendar.YEAR));
                if (calendar.get(Calendar.MONTH)<9)
                    date+="-0"+String.valueOf(calendar.get(Calendar.MONTH)+1); else date+="-"+String.valueOf(calendar.get(Calendar.MONTH)+1);
                if (calendar.get(Calendar.DAY_OF_MONTH)<10)
                    date+="-0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); else date+="-"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                if (jsn.getString("date").equals(date)) {

                    editMass.setText(jsn.getString("mass"));
                    dayNote.setText(jsn.getString("note"));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        initAntropometry();
        addRecyclerOnClickListeners();

        todayDate.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "." + getMonth(calendar.get(Calendar.MONTH)) + "." + calendar.get(Calendar.YEAR));

        getCaloriesPerDay();
    }

    private void initAntropometry(){
        try {
            JSONObject jsn;
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);

                String date = String.valueOf(calendar.get(Calendar.YEAR));
                if (calendar.get(Calendar.MONTH)<9)
                    date+="-0"+String.valueOf(calendar.get(Calendar.MONTH)+1); else date+="-"+String.valueOf(calendar.get(Calendar.MONTH)+1);
                if (calendar.get(Calendar.DAY_OF_MONTH)<10)
                    date+="-0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); else date+="-"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                if (jsn.getString("date").equals(date)) {

                    rLeg.setText(jsn.getString("rLeg"));
                    rHand.setText(jsn.getString("rHand"));
                    lLeg.setText(jsn.getString("lLeg"));
                    chest.setText(jsn.getString("chest"));
                    lHand.setText(jsn.getString("lHand"));
                    waist.setText(jsn.getString("waist"));
                    butt.setText(jsn.getString("butt"));
                    calves.setText(jsn.getString("calves"));
                    shoulders.setText(jsn.getString("shoulders"));
                }
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addViews(){
        rLeg = (EditText) findViewById(R.id.edit_right_leg);
        lLeg = (EditText) findViewById(R.id.edit_left_leg);
        rHand = (EditText) findViewById(R.id.edit_right_hand);
        lHand = (EditText) findViewById(R.id.edit_left_hand);
        chest = (EditText) findViewById(R.id.edit_chest);
        waist = (EditText) findViewById(R.id.edit_waist);
        butt = (EditText) findViewById(R.id.edit_butt);
        calves = (EditText) findViewById(R.id.edit_calves);
        shoulders = (EditText) findViewById(R.id.edit_shoulders);

        foodRecyclerView = (RecyclerView) findViewById(R.id.food_busket_recycler_view);
        antropometry = (LinearLayout) findViewById(R.id.antropometry_today);
        actionRecyclerView = (RecyclerView) findViewById(R.id.action_busket_recycler_view);
        scrlView = (NestedScrollView) findViewById(R.id.today_scroll_view);

        editMass = (EditText) findViewById(R.id.edit_mass);
        dayNote = (EditText) findViewById(R.id.DayNote);
        todayDate = (TextView) findViewById(R.id.todayDate);
        foodCalories = (TextView) findViewById(R.id.foodCalories);
        sportCalories = (TextView) findViewById(R.id.sportCalories);
        normCalories = (TextView) findViewById(R.id.normaCaloriesText);
        protein = (TextView) findViewById(R.id.ProteinText);
        fats = (TextView) findViewById(R.id.FatsText);
        carbs = (TextView) findViewById(R.id.CarbsText);
        linearLayout = (LinearLayout) findViewById(R.id.setting_layout);
    }

    private void addRecyclerOnClickListeners(){
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
    }

    public  void onAddFoodClc(View view){
        Intent intent = new Intent(getApplicationContext(), RecycleFoodCatalogActivity.class);
        startActivity(intent);
    }

    public  void onAddActionsClc(View view){
        Intent intent = new Intent(getApplicationContext(), RecycleActionCatalogActivity.class);
        startActivity(intent);
    }

    public void onMainFABClc(View view){
        if(FABFlag){
            linearLayout.setVisibility(View.INVISIBLE);
            FABFlag = false;

        } else {
            linearLayout.setVisibility(View.VISIBLE);
            FABFlag = true;
        }
    }

    private void getCaloriesPerDay(){
        Post log = new Post();
        args[0] = "http://caloriesdiary.ru/calories/get_per_day";
        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
        args[2] = FirebaseInstanceId.getInstance().getToken();
        log.execute(args);

        try {
            JSONObject resp = log.get();
            normCalories.setText(resp.getInt("result") + " ккал");

            protein.setText(resp.getInt("protein") + " г.");
            fats.setText(resp.getInt("fats") + " г.");
            carbs.setText(resp.getInt("carbs") + " г.");
        }catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        try{

            File f = new File(getCacheDir(), "Food.txt");
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inObject = new ObjectInputStream(in);
            String text = inObject.readObject().toString();
            inObject.close();

            JSONObject jsn = new JSONObject(text);
            jsonFood = jsn.getJSONArray("food");

            for (int j = 0; j < jsonFood.length(); j++) {
                sum += Integer.parseInt(jsonFood.getJSONObject(j).get("calories").toString());
            }

            foodCalories.setText(sum + " ккал");


            f = new File(getCacheDir(), "Actions.txt");
            in = new FileInputStream(f);
            inObject = new ObjectInputStream(in);
            text = inObject.readObject().toString();
            inObject.close();

            jsn = new JSONObject(text);
            jsonAction = jsn.getJSONArray("active");


            for (int j = 0; j < jsonAction.length(); j++) {
                sum1 += Integer.parseInt(jsonAction.getJSONObject(j).get("calories").toString());
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
                sportCalories.setText("");
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

    public void onTodayFoodBtnClc(View v) {
        initFoodData();

        foodRecyclerView.setAdapter(foodAdapter);
    }

    public void todayAntrClc(View view) {

        if (antropometryFlag) {
            antropometry.setVisibility(View.VISIBLE);
            Toast.makeText(this,String.valueOf(antropometry.getHeight()),Toast.LENGTH_LONG).show();
            scrlView.scrollBy(0, 200);
            antropometryFlag = false;
        } else {
            antropometry.setVisibility(View.GONE);
            antropometryFlag = true;
        }


    }



    public void onTodayActivityBtnClc(View v) {
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

                Post log = new Post();
                args[0] = "http://caloriesdiary.ru/calories/get_per_day";
                args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                args[2] = FirebaseInstanceId.getInstance().getToken();
                log.execute(args);
                JSONObject jOb;
                try {
                    jOb = log.get();
                    listActive.add(new ActionItem("Сон", Float.valueOf(jOb.getString("dreamCalories")), 5));
                }catch (Exception e){

                }

                jOb = new JSONObject(resp);
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

    @Override
    protected void onStop() {
        try {
            JSONObject jsn = new JSONObject();
            File f = new File(getCacheDir(), "Today_params.txt");
            f.createNewFile();

            jsn.put("id", String.valueOf(sharedPref.getInt("PROFILE_ID", 0)));
            jsn.put("day_calories", normCalories.getText().toString());
            jsn.put("mass", editMass.getText().toString());
            jsn.put("note", dayNote.getText().toString());
            jsn.put("active", jsonAction);
            jsn.put("food", jsonFood);
            jsn.put("active_sum", String.valueOf(sum1));
            jsn.put("food_sum", String.valueOf(sum));
            jsn.put("carbs", carbs.getText().toString());
            jsn.put("protein", protein.getText().toString());
            jsn.put("fats", fats.getText().toString());
            jsn.put("instanceToken", FirebaseInstanceId.getInstance().getToken());

            String date = String.valueOf(calendar.get(Calendar.YEAR));
            if (calendar.get(Calendar.MONTH)<9)
                date+="-0"+String.valueOf(calendar.get(Calendar.MONTH)+1); else date+="-"+String.valueOf(calendar.get(Calendar.MONTH)+1);
            if (calendar.get(Calendar.DAY_OF_MONTH)<10)
                date+="-0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); else date+="-"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

            jsn.put("date", date);

            if (!rLeg.getText().toString().equals(""))
                jsn.put("rLeg", rLeg.getText().toString());
            else jsn.put("rLeg", "0");
            if (!lLeg.getText().toString().equals(""))
                jsn.put("lLeg", lLeg.getText().toString());
            else jsn.put("lLeg", "0");
            if (!rHand.getText().toString().equals(""))
                jsn.put("rHand", rHand.getText().toString());
            else jsn.put("rHand", "0");
            if (!lHand.getText().toString().equals(""))
                jsn.put("lHand", lHand.getText().toString());
            else jsn.put("lHand", "0");
            if (!calves.getText().toString().equals(""))
                jsn.put("calves", calves.getText().toString());
            else jsn.put("calves", "0");
            if (!shoulders.getText().toString().equals(""))
                jsn.put("shoulders", shoulders.getText().toString());
            else jsn.put("shoulders", "0");
            if (!butt.getText().toString().equals(""))
                jsn.put("butt", butt.getText().toString());
            else jsn.put("butt", "0");
            if (!waist.getText().toString().equals(""))
                jsn.put("waist", waist.getText().toString());
            else jsn.put("waist", "0");
            if (!chest.getText().toString().equals(""))
                jsn.put("chest", chest.getText().toString());
            else jsn.put("chest", "0");


            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream outObject = new ObjectOutputStream(out);
            outObject.writeObject(jsn.toString());
            outObject.flush();
            out.getFD().sync();
            outObject.close();


            SaveTodayParams saveBackUp = new SaveTodayParams();


            saveBackUp.execute(jsn);


            Toast.makeText(this, saveBackUp.get(), Toast.LENGTH_SHORT).show();




        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        super.onStop();
    }
}

//class scrlTask extends AsyncTask<Boolean, Void, Void>{
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//    }
//
//    @Override
//    protected Void doInBackground(Boolean... booleen) {
//
//        if (booleen[0])
//
//
//        return null;
//    }
//}
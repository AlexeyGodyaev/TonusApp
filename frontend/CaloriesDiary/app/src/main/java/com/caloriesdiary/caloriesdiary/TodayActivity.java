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

<<<<<<< HEAD
import java.io.BufferedWriter;
=======
>>>>>>> Alex's-branch
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
<<<<<<< HEAD
import java.io.OutputStreamWriter;
=======
>>>>>>> Alex's-branch
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TodayActivity extends FragmentActivity {

    List<FoodItem> list = new ArrayList<FoodItem>();
    List<ActionItem> listActive = new ArrayList<ActionItem>();
    TextView todayDate, dayOfTheWeek, countOfDays, targetText;
    Button activityBtn, todayFoodBtn, addFoodBtn;
<<<<<<< HEAD
    public ListView foodBasketList;
   // public FoodAdapter adapter = new FoodAdapter(getApplicationContext(), initData());


    private boolean flag = true;
    public FragmentManager manager;
    public FragmentTransaction transaction;
    public FoodBasketFragment foodBasketFragment;
    public FoodAdapter adapter;
=======
    ListView foodBasketList, activeBasketList;
    FoodAdapter adapter; //прихуярю сюда фрагмент чтоб блять можно было запустить обе листвьюхи
    ActionsAdapter actionsAdapter;

    private boolean foodFlag = false, activeFlag = false;
>>>>>>> Alex's-branch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);
        adapter = new FoodAdapter(this, initFoodData());
        actionsAdapter = new ActionsAdapter(this, initActiveData());

<<<<<<< HEAD
        manager = getSupportFragmentManager();
        foodBasketFragment = new FoodBasketFragment();


       // foodBasketList.setAdapter(adapter);
        try
        {
            JSONArray array = new JSONArray();
            JSONObject jsn = new JSONObject();
            JSONObject mainjsn = new JSONObject();
           jsn.put("fats","lox");
            jsn.put("category_id","fock");
            array.put(jsn);
            mainjsn.put("food",array);
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                    openFileOutput("Food.txt",MODE_PRIVATE)));

            File f = new File(getCacheDir(), "Food2.txt");
            if(f.exists())
                Toast.makeText(getApplicationContext(),"Файл есть",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(),"Файла нетъ",Toast.LENGTH_LONG).show();
            FileOutputStream out=new FileOutputStream(f);
            ObjectOutputStream outObject=new ObjectOutputStream(out);
            outObject.writeObject(mainjsn.toString());
            outObject.flush();
            out.getFD().sync();
            outObject.close();




            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inObject = new ObjectInputStream(in);

            Toast.makeText(getApplicationContext(),inObject.readObject().toString(),Toast.LENGTH_LONG).show();
//            writer.write(mainjsn.toString());
//            writer.close();
            //initData();
            inObject.close();
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }


=======
>>>>>>> Alex's-branch

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



<<<<<<< HEAD
        transaction.commit();
        try
        {

            adapter = new FoodAdapter(this, initData());

            foodBasketList = (ListView) findViewById(R.id.foodBasketList);
            if (foodBasketList == null)
                Toast.makeText(getApplicationContext(),foodBasketFragment.getView().toString(),Toast.LENGTH_LONG).show();
            foodBasketList.setAdapter(adapter);
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

=======
    public void onTodayFoodBtnClc(View v){
            initFoodData();
            foodBasketList.setAdapter(adapter);
>>>>>>> Alex's-branch
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
            File f = new File(getCacheDir(), "Food.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                resp = inObject.readObject().toString();
                inObject.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        if (resp != null&&foodFlag == true) {
            try {
                JSONObject jOb = new JSONObject(resp);
                JSONArray jArr = jOb.getJSONArray("food");
                for (int i = 0; i < jArr.length(); i++) {
                    try {
//                        Integer i1 = new Integer(jArr.getJSONObject(i).getString("category_id"));
//                        id = i1;
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
                    //list.add(new FoodItem("hui", 1f, 2f, 3f, 4, 1435f));
                    list.add(new FoodItem(foodName,b,j,u,id,calories));
                }
            } catch (JSONException jEx) {
                Toast.makeText(getApplicationContext(), jEx.toString(), Toast.LENGTH_SHORT).show();
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
//                        actionName = jArr.getJSONObject(i).getString("name");
//                        f1 = new Float(jArr.getJSONObject(i).getString("calories"));
//                        calories = f1;
//                    } catch (NumberFormatException e) {
//                        System.err.println("Неверный формат строки!");
//                    }
                    //if(b!=0||j!=0||u!=0||calories!=0)
                    listActive.add(new ActionItem("hui", 3f, 4));
                    //list.add(new FoodItem(actionName,calories,id));
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

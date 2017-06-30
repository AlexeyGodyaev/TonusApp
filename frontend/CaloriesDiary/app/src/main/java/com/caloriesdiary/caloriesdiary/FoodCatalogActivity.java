package com.caloriesdiary.caloriesdiary;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FoodCatalogActivity extends FragmentActivity {
    boolean flag = true;
    ListView listView;
    EditText srch;
    private FilterFragment filterFragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_catalog_layout);

        filterFragment = new FilterFragment();
        manager = getSupportFragmentManager();

        srch = (EditText) findViewById(R.id.srchFood);
        srch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (i == KeyEvent.KEYCODE_ENTER)){
                    //высылаем пост с именем
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.foodList);

        FoodAdapter adapter = new FoodAdapter(this, initData());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsn = new JSONObject();
                TextView txtName = (TextView) view.findViewById(R.id.productName);
                TextView txtBJU = (TextView) view.findViewById(R.id.bJU);
                TextView txtCalories = (TextView) view.findViewById(R.id.productCalories);
                try {
                    jsn.put("name",txtName.getText().toString());
                    jsn.put("bju",txtBJU.getText().toString());
                    jsn.put("calories",txtCalories.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput("Food.txt",MODE_APPEND)));

                    writer.write(jsn.toString());
                    writer.close();

                    FileInputStream fin = null;
                    fin = openFileInput("Food.txt");
                    byte[] bytes = new byte[fin.available()];
                    fin.read(bytes);
                    String text = new String (bytes);

                    Toast.makeText(getApplicationContext(), text , Toast.LENGTH_LONG).show();
                }
                catch (FileNotFoundException fEx){

                }
                catch (IOException iEx){

                }
            }
        });
    }

    public void onClcFilterButton(View view){
        transaction = manager.beginTransaction();

        if(flag == true){
        transaction.add(R.id.filterFragmentFood, filterFragment); flag = false;}
        else {transaction.remove(filterFragment);  flag = true;}

        transaction.commit();
    }

    public  String getFood() throws InterruptedException, ExecutionException{
        GetFood get = new GetFood();
        get.execute("http://192.168.1.205/food/get_food");

        return get.get().toString();
    }

<<<<<<< HEAD
=======

>>>>>>> Alex's-branch
    private List<FoodItem> initData() {
        List<FoodItem> list = new ArrayList<FoodItem>();
        String resp = null;
        String foodName;
        Float b=new Float(0), j=new Float(0), u=new Float(0), calories=new Float(0);
        Integer id=0;

        try {
            resp = getFood();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resp != null)
            while(resp.length()>10){
            try {
                Integer f1 = new Integer(resp.substring(0,resp.indexOf(',')));
                id=f1;
            } catch (NumberFormatException e) {
                System.err.println("Неверный формат строки!");
            }
            resp=resp.substring(resp.indexOf(',')+1);

            foodName = resp.substring(0,resp.indexOf(','));
            resp=resp.substring(resp.indexOf(',')+1);



            try {
                Float f1 = new Float(resp.substring(0,resp.indexOf(',')));
                b = f1;
                resp=resp.substring(resp.indexOf(',')+1);
                f1 = new Float(resp.substring(0,resp.indexOf(',')));
                j=f1;
                resp=resp.substring(resp.indexOf(',')+1);
                f1 = new Float(resp.substring(0,resp.indexOf(',')));
                u=f1;
                resp=resp.substring(resp.indexOf(',')+1);
                f1 = new Float(resp.substring(0,resp.indexOf(';')));
                calories=f1;
            } catch (NumberFormatException e) {
                System.err.println("Неверный формат строки!");
            }

            resp=resp.substring(resp.indexOf(';')+1);

                if(b!=0||j!=0||u!=0||calories!=0)
                list.add(new FoodItem(foodName,b,j,u,id,calories));
        }

        return list;
    }
}

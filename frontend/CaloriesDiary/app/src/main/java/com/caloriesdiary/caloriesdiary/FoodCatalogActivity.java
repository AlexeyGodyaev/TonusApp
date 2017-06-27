package com.caloriesdiary.caloriesdiary;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FoodCatalogActivity extends Activity{

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_catalog_layout);

        listView = (ListView) findViewById(R.id.foodList);



        FoodAdapter adapter = new FoodAdapter(this, initData());
        listView.setAdapter(adapter);
    }

    public  String getFood() throws InterruptedException, ExecutionException{
        Get get = new Get();
        get.execute("http://192.168.1.205/food/get_food");

        return get.get().toString();
    }

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

package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by emilg on 10.07.2017.
 */

public class RecycleFoodCatalogActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_food_catalog_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("СПРАВОЧНИК БЛЮД");

        mRecyclerView =  (RecyclerView) findViewById(R.id.food_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecycleFoodAdapter(initData());
        mRecyclerView.setAdapter(mAdapter);
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

    public JSONArray getFood() throws InterruptedException,
            ExecutionException {
        GetFood get = new GetFood();
        get.execute("http://94.130.12.179/food/get_food",String.valueOf(-1));

        return get.get();
    }

    private List<FoodItem> initData() {
        List<FoodItem> list = new ArrayList<FoodItem>();
        JSONArray resp = null;
        String foodName = null;
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
            for(int i = 0; i<resp.length(); i++){
                try {
                    Integer i1 = new Integer(resp.getJSONObject(i).getString("category_id"));
                    id=i1;
                    foodName = resp.getJSONObject(i).getString("name");
                    Float f1 = new Float(resp.getJSONObject(i).getString("protein"));
                    b = f1;
                    f1 = new Float(resp.getJSONObject(i).getString("fats"));
                    j=f1;
                    f1 = new Float(resp.getJSONObject(i).getString("carbs"));
                    u=f1;
                    f1 = new Float(resp.getJSONObject(i).getString("calories"));
                    calories=f1;
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат строки!");
                } catch (JSONException jEx){
                    Toast.makeText(getApplicationContext(),jEx.toString(), Toast.LENGTH_SHORT).show();
                }
                if(b!=0||j!=0||u!=0||calories!=0)
                    list.add(new FoodItem(foodName,b,j,u,id,calories));
            }

        return list;
    }

}

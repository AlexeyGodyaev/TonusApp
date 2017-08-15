package com.caloriesdiary.caloriesdiary.Activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Adapters.CustomFoodAdapter;
import com.caloriesdiary.caloriesdiary.HTTP.Get;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.HTTP.GetCategories;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.Adapters.RecycleFoodAdapter;
import com.caloriesdiary.caloriesdiary.RecyclerTouchListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class FoodBuilderActivity extends AppCompatActivity implements CallBackListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView dialogRecyclerView;
    private RecyclerView.Adapter dialogAdapter;
    private RecyclerView.LayoutManager dialogLayoutManager;

    private RecyclerView ingRecyclerView;
    private RecyclerView.Adapter ingAdapter;
    private RecyclerView.LayoutManager ingLayoutManager;

    public CheckBox buildercheck1,buildercheck2;
    public Spinner spinner1,spinner2;

    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;

    EditText nameedit;

    List<FoodItem> list = new ArrayList<>();
    List<FoodItem> item = new ArrayList<>();
    Post post;
    Get get;
    CallBackListener listener;

    int offset = 0;
    boolean send = true,changescrollpos = false, updialog = false;
    int buff=0;
    String searchquery = "",searchcatid = "",searchsortkcal = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {


            setContentView(R.layout.food_builder_layout);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            handleIntent(getIntent());
            initTabs();
            initObjects();
            initData();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void initData() {
        post = new Post();
        list = new ArrayList<>();
        String args[] = new String[8];
        args[0] = "http://caloriesdiary.ru/food/get_food";
        args[1] = String.valueOf(offset); //offset
        args[2] = searchquery; //query
        args[3] = ""; //categ_id
        args[4] = ""; //sort_names
        args[5] = ""; //sort_calories
        args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
        args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
        listener = this;
        post.setListener(listener);
        post.execute(args);

    }
    @Override
    public void callback() {
        fillList();
        if(changescrollpos)
        {
            ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPosition(buff);
            changescrollpos = false;
        }


    }

    private void fillList() {
        try
        {
            JSONObject json = post.get();

            if(json.getString("status").equals("1"))
            {

                FoodItem foodItemtoAdd = new FoodItem();
                JSONArray jarr = json.getJSONArray("food");
                for(int i = 0; i < jarr.length(); i++)
                {
                    foodItemtoAdd = new FoodItem();

                    foodItemtoAdd.setId(Integer.valueOf(jarr.getJSONObject(i).getString("food_id")));
                    foodItemtoAdd.setName(jarr.getJSONObject(i).getString("name"));
                    foodItemtoAdd.setB(Float.valueOf(jarr.getJSONObject(i).getString("protein")));
                    foodItemtoAdd.setJ(Float.valueOf(jarr.getJSONObject(i).getString("fats")));
                    foodItemtoAdd.setU(Float.valueOf(jarr.getJSONObject(i).getString("carbs")));
                    foodItemtoAdd.setCalories(Float.valueOf(jarr.getJSONObject(i).getString("calories")));
                    foodItemtoAdd.setCategoryId(Integer.valueOf(jarr.getJSONObject(i).getString("id")));
                    list.add(foodItemtoAdd);

                }
                JSONArray customjarr = json.getJSONArray("custom_food");
                for(int i = 0; i < customjarr.length(); i++) {
                    foodItemtoAdd = new FoodItem();
                    foodItemtoAdd.setId(Integer.valueOf(customjarr.getJSONObject(i).getString("id")));
                    foodItemtoAdd.setName(customjarr.getJSONObject(i).getString("name") + " (custom)");
                    foodItemtoAdd.setB(0f);
                    foodItemtoAdd.setJ(0f);
                    foodItemtoAdd.setU(0f);
                    foodItemtoAdd.setCalories(0f);
                    foodItemtoAdd.setCategoryId(0);
                    list.add(foodItemtoAdd);
                }
                mAdapter = new RecycleFoodAdapter(list);
                mRecyclerView.setAdapter(mAdapter);
                if(updialog)
                {
                    updialog = false;
                    dialogAdapter = new CustomFoodAdapter(list);
                    dialogRecyclerView.setAdapter(dialogAdapter);
                }
            }



        }
        catch (Exception e)
        {
            Toast.makeText(this,"Filllist error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            MenuInflater inflater = getMenuInflater();

            inflater.inflate(R.menu.menu_main, menu);

            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        //Toast.makeText(this, intent.getStringExtra(SearchManager.QUERY), Toast.LENGTH_SHORT).show();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchquery = query;
            list = new ArrayList<>();
            post = new Post();
            String args[] = new String[8];
            args[0] = "http://caloriesdiary.ru/food/get_food";
            args[1] = String.valueOf(offset); //offset
            args[2] = searchquery; //query
            args[3] = searchcatid; //categ_id
            args[4] = "1"; //sort_names
            args[5] = searchsortkcal; //sort_calories
            args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
            args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
            post.setListener(listener);
            post.execute(args);

        }
    }

    public void  initObjects()
    {

        buildercheck1 = (CheckBox) findViewById(R.id.builder_checkbox1);
        buildercheck2 = (CheckBox) findViewById(R.id.builder_checkbox2);
        spinner1 = (Spinner) findViewById(R.id.builder_spinner1);
        spinner2 = (Spinner) findViewById(R.id.builder_spinner2);
        final ArrayList<String> categories = new ArrayList<>();

        try
        {


            get = new Get();
            get.execute("http://caloriesdiary.ru/food/get_food_categories");

            //Toast.makeText(this,"ans:" + get.get().toString(), Toast.LENGTH_LONG).show();
            JSONObject json = get.get();
            if(json.getString("status").equals("1")) {


                //  Toast.makeText(this, "json: " + json.toString(), Toast.LENGTH_LONG).show();
                final JSONArray jarr = json.getJSONArray("categories");

                for (int i = 0; i < jarr.length(); i++) {
                    categories.add(jarr.getJSONObject(i).getString("categ_name"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
                spinner1.setAdapter(adapter);

                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String text = spinner1.getSelectedItem().toString();

                        for (int k = 0; k < jarr.length(); k++) {
                            try {
                                if (jarr.getJSONObject(k).getString("categ_name").equals(text) && buildercheck1.isChecked()) {
                                    list = new ArrayList<>();
                                    post = new Post();
                                    offset = 0;
                                    searchcatid = jarr.getJSONObject(k).getString("id");
                                    String args[] = new String[10];
                                    args[0] = "http://caloriesdiary.ru/food/get_food";
                                    args[1] = String.valueOf(offset);
                                    args[2] = searchquery; //строка поиска
                                    args[3] = searchcatid; //id категории
                                    args[4] = "1"; //сорт имени
                                    args[5] = searchsortkcal; //сорт ккал
                                    args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
                                    args[7] = FirebaseInstanceId.getInstance().getToken();
                                    post.setListener(listener);
                                    post.execute(args);
                                }
                            } catch (Exception e2) {

                            }


                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast.makeText(FoodBuilderActivity.this, "Пустота", Toast.LENGTH_LONG).show();
                    }
                });
            }

            String values[] = {"По возрастанию", "По убыванию"};
            ArrayAdapter<String> kcaladapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
            spinner2.setAdapter(kcaladapter);

            if (!buildercheck1.isChecked())
            {
                spinner1.setEnabled(false);
            }
            if (!buildercheck2.isChecked())
            {
                spinner2.setEnabled(false);
            }

            buildercheck1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        spinner1.setEnabled(true);


                    }
                    else
                    {
                        spinner1.setEnabled(false);
                        searchcatid = "";
                        post = new Post();
                        list = new ArrayList<>();
                        String args[] = new String[8];
                        args[0] = "http://caloriesdiary.ru/food/get_food";
                        args[1] = String.valueOf(offset); //offset
                        args[2] = searchquery; //query
                        args[3] = searchcatid; //categ_id
                        args[4] = "1"; //sort_names
                        args[5] = searchsortkcal; //sort_calories
                        args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                        args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                        post.setListener(listener);
                        post.execute(args);


                    }

                }
            });

            buildercheck2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        spinner2.setEnabled(true);

                        if(spinner2.getSelectedItemPosition()==0)
                        {
                            searchsortkcal = "1";
                            post = new Post();
                            list = new ArrayList<>();
                            String args[] = new String[8];
                            args[0] = "http://caloriesdiary.ru/food/get_food";
                            args[1] = String.valueOf(offset); //offset
                            args[2] = searchquery; //query
                            args[3] = searchcatid; //categ_id
                            args[4] = "1"; //sort_names
                            args[5] = searchsortkcal; //sort_calories
                            args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                            args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                            post.setListener(listener);
                            post.execute(args);
                        }
                        else
                        {
                            searchsortkcal = "2";
                            post = new Post();
                            list = new ArrayList<>();
                            String args[] = new String[8];
                            args[0] = "http://caloriesdiary.ru/food/get_food";
                            args[1] = String.valueOf(offset); //offset
                            args[2] = searchquery; //query
                            args[3] = searchcatid; //categ_id
                            args[4] = "1"; //sort_names
                            args[5] = searchsortkcal; //sort_calories
                            args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                            args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                            post.setListener(listener);
                            post.execute(args);
                        }

                    }
                    else
                    {
                        spinner2.setEnabled(false);
                        searchsortkcal = "";
                        post = new Post();
                        list = new ArrayList<>();
                        String args[] = new String[8];
                        args[0] = "http://caloriesdiary.ru/food/get_food";
                        args[1] = String.valueOf(offset); //offset
                        args[2] = searchquery; //query
                        args[3] = searchcatid; //categ_id
                        args[4] = "1"; //sort_names
                        args[5] = searchsortkcal; //sort_calories
                        args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                        args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                        post.setListener(listener);
                        post.execute(args);
                    }

                }
            });

            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (spinner2.isEnabled()) {


                        if (spinner2.getSelectedItemPosition() == 0) {
                            searchsortkcal = "1";
                            post = new Post();
                            list = new ArrayList<>();
                            String args[] = new String[8];
                            args[0] = "http://caloriesdiary.ru/food/get_food";
                            args[1] = String.valueOf(offset); //offset
                            args[2] = searchquery; //query
                            args[3] = searchcatid; //categ_id
                            args[4] = "1"; //sort_names
                            args[5] = searchsortkcal; //sort_calories
                            args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0)); //id
                            args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                            post.setListener(listener);
                            post.execute(args);
                        } else {
                            searchsortkcal = "2";
                            post = new Post();
                            list = new ArrayList<>();
                            String args[] = new String[8];
                            args[0] = "http://caloriesdiary.ru/food/get_food";
                            args[1] = String.valueOf(offset); //offset
                            args[2] = searchquery; //query
                            args[3] = searchcatid; //categ_id
                            args[4] = "1"; //sort_names
                            args[5] = searchsortkcal; //sort_calories
                            args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0)); //id
                            args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                            post.setListener(listener);
                            post.execute(args);
                            Toast.makeText(FoodBuilderActivity.this, "kcal: " + searchsortkcal, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });





        }
        catch (Exception e)
        {
            Toast.makeText(this,"InitObjects error: " + e.toString(), Toast.LENGTH_LONG).show();
        }




        mRecyclerView = (RecyclerView) findViewById(R.id.food_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ingRecyclerView = (RecyclerView) findViewById(R.id.recycle_ingredients);
        ingRecyclerView.setHasFixedSize(true);

        ingLayoutManager = new LinearLayoutManager(this);
        ingRecyclerView.setLayoutManager(ingLayoutManager);

        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
        editor = sharedPref.edit();

        nameedit = (EditText) findViewById(R.id.custom_food_name);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (totalItemCount - 1 == lastVisibleItems && send) {
                    buff = lastVisibleItems - visibleItemCount + 1;

                    send = false;
                    offset++;

                    post = new Post();
                    String args[] = new String[8];
                    args[0] = "http://caloriesdiary.ru/food/get_food";
                    args[1] = String.valueOf(offset); //offset
                    args[2] = searchquery; //query
                    args[3] = searchcatid; //categ_id
                    args[4] = ""; //sort_names
                    args[5] = ""; //sort_calories
                    args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                    args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                    post.setListener(listener);
                    changescrollpos = true;
                    post.execute(args);

                }

            }
        });

    }
    public void initTabs()
    {
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup();
        TabHost.TabSpec tabSpec;

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("tag1");
        // название вкладки
        tabSpec.setIndicator("Справочник");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.tab1);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");

        tabSpec.setIndicator("Холодильник");

        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

//        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            public void onTabChanged(String tabId) {
//                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void onAddFood (View view)
    {
        View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_food_dialoglayout,null);

        final EditText edittext =  content.findViewById(R.id.custom_food_search);


        dialogRecyclerView =  content.findViewById(R.id.custom_food_recycle);
        dialogRecyclerView.setHasFixedSize(true);

        dialogLayoutManager = new LinearLayoutManager(this);
        dialogRecyclerView.setLayoutManager(dialogLayoutManager);

        dialogRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = dialogLayoutManager.getChildCount();
                int totalItemCount = dialogLayoutManager.getItemCount();
                int lastVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                edittext.setText(String.valueOf(visibleItemCount) + ":" +String.valueOf(totalItemCount) + ":"+String.valueOf(lastVisibleItems));
                if (totalItemCount -1 == lastVisibleItems && send){
                    buff = lastVisibleItems-visibleItemCount+1;
                    //get = new GetFood();
                    send = false;
                    offset++;
                    updialog = true;
                    post = new Post();
                    String args[] = new String[8];
                    args[0] = "http://caloriesdiary.ru/food/get_food";
                    args[1] = String.valueOf(offset); //offset
                    args[2] = ""; //query
                    args[3] = ""; //categ_id
                    args[4] = ""; //sort_names
                    args[5] = ""; //sort_calories
                    args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                    args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                    post.setListener(listener);
                    post.execute(args);
                    ((LinearLayoutManager)dialogRecyclerView.getLayoutManager()).scrollToPosition(buff);
                    Toast.makeText(FoodBuilderActivity.this, String.valueOf(buff), Toast.LENGTH_SHORT).show();
                }

            }
        });

        updialog = true;
        send = true;
        offset = 0;
        post = new Post();
        String args[] = new String[8];
        args[0] = "http://caloriesdiary.ru/food/get_food";
        args[1] = String.valueOf(offset); //offset
        args[2] = ""; //query
        args[3] = ""; //categ_id
        args[4] = ""; //sort_names
        args[5] = ""; //sort_calories
        args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
        args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
        post.setListener(listener);
        post.execute(args);



        dialogRecyclerView.addOnItemTouchListener( new RecyclerTouchListener(this, dialogRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final CheckBox checkbox = (CheckBox) view.findViewById(R.id.add_food_checkbox);
                final TextView textB = (TextView) view.findViewById(R.id.viewB);
                final TextView textJ = (TextView) view.findViewById(R.id.viewJ);
                final TextView textU = (TextView) view.findViewById(R.id.viewU);
                final TextView textKc = (TextView) view.findViewById(R.id.viewKc);
                final TextView textId = (TextView) view.findViewById(R.id.viewID);

                if(!checkbox.isChecked())
                {
                    Toast.makeText(FoodBuilderActivity.this, "Добавить " + checkbox.getText().toString() , Toast.LENGTH_SHORT).show();
                    item.add(new FoodItem(Integer.valueOf(textId.getText().toString()),checkbox.getText().toString(),Float.valueOf(textB.getText().toString()),Float.valueOf(textJ.getText().toString()),Float.valueOf(textU.getText().toString()),0,Float.valueOf(textKc.getText().toString())));

                }
                else
                {
                    Toast.makeText(FoodBuilderActivity.this,"Удалить " + checkbox.getText().toString(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < item.size(); i++ )
                    {
                        if( item.get(i).getName().equals(checkbox.getText().toString()))
                        {
                            item.remove(i);
                        }
                    }

                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));




                AlertDialog.Builder builder = new AlertDialog.Builder(FoodBuilderActivity.this);
        builder.setTitle("Добавление блюда")
                .setView(content)
                .setPositiveButton("Принять", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(FoodBuilderActivity.this, edittext.getText().toString(), Toast.LENGTH_SHORT).show();
                        ingAdapter = new RecycleFoodAdapter(item);
                        ingRecyclerView.setAdapter(ingAdapter);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onAddinDB(View view)
    {

        try
        {
            JSONArray jsnArray = new JSONArray();
            JSONObject jsn = new JSONObject();
            for (int i = 0;i < item.size(); i++) {
                jsnArray.put(String.valueOf(item.get(i).getId()));
            }
            jsn.put("ingredients",jsnArray);
           // Toast.makeText(this, jsn.toString(), Toast.LENGTH_LONG).show();
        Post post = new Post();

        String args[] = new String[4];
        args[0] = "http://caloriesdiary.ru/food/save_custom_dish";
        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
        args[2] = nameedit.getText().toString();
        args[3] = jsn.toString();
            post.setListener(listener);
            post.execute(args);
            String ans = post.get().toString();
            Toast.makeText(this, ans, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {

        }



    }

}

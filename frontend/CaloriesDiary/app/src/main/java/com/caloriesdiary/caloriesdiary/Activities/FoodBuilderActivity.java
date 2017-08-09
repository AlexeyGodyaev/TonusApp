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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Adapters.CustomFoodAdapter;
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
    CallBackListener listener;
    GetFood get;
    int offset = 0;
    boolean send = true;
    int buff=0;
    String searchquery = "";
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


            get = new FoodBuilderActivity.GetFood();
            String args[] = new String[6];
            args[0] = "http://caloriesdiary.ru/food/get_food";
            args[1] = String.valueOf(offset);
            args[2] = ""; //строка поиска
            args[3] = ""; //id категории
            args[4] = ""; //сорт имени
            args[5] = ""; //сорт ккал
            get.execute(args);
            //get.execute("http://caloriesdiary.ru/food/get_food", String.valueOf(offset),"","","","");

            Toast.makeText(this, get.get().toString(), Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void initData() {
        post = new Post();
        String args[] = new String[8];
        args[0] = "http://caloriesdiary.ru/food/get_food";
        args[1] = ""; //offset
        args[2] = ""; //query
        args[3] = ""; //categ_id
        args[4] = ""; //sort_names
        args[5] = ""; //sort_calories
        args[6] = ""; //id
        args[7] = ""; //instanceToken
        listener = this;
        post.setListener(listener);
        post.execute(args);

    }
    @Override
    public void callback() {
        fillList();
    }

    private void fillList() {
        try
        {
            JSONObject json = post.get();
            list = new ArrayList<>();
            //Toast.makeText(this, json.toString(), Toast.LENGTH_SHORT).show();
            if(json.getString("status").equals("1"))
            {
                JSONArray jarr = json.getJSONArray("food");
                for(int i = 0; i < jarr.length(); i++)
                {
                    list.add();
                }

            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
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
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        Toast.makeText(this, intent.getStringExtra(SearchManager.QUERY), Toast.LENGTH_SHORT).show();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchquery = query;
           // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            list = new ArrayList<>();

            get = new FoodBuilderActivity.GetFood();
            offset = 0;
            String args[] = new String[10];
            args[0] = "http://caloriesdiary.ru/food/get_food";
            args[1] = String.valueOf(offset);
            args[2] = searchquery; //строка поиска
            args[3] = ""; //id категории
            args[4] = ""; //сорт имени
            args[5] = ""; //сорт ккал
            args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[7] = FirebaseInstanceId.getInstance().getToken();
            get.execute(args);

        }
    }

    public void initObjects()
    {
        buildercheck1 = (CheckBox) findViewById(R.id.builder_checkbox1);
        buildercheck2 = (CheckBox) findViewById(R.id.builder_checkbox2);
        spinner1 = (Spinner) findViewById(R.id.builder_spinner1);
        spinner2 = (Spinner) findViewById(R.id.builder_spinner2);
        final ArrayList<String> categories = new ArrayList<>();

        try
        {
            GetCategories getcategories = new GetCategories();
            getcategories.execute("http://caloriesdiary.ru/food/get_food_categories");
            String ans = getcategories.get();
            final JSONArray jsarray = new JSONArray(ans);

            categories.add("-");
            for(int i = 0; i < jsarray.length(); i++)
            {
                    categories.add(jsarray.getJSONObject(i).getString("categ_name"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
            spinner1.setAdapter(adapter);

            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String text = spinner1.getSelectedItem().toString();
                    Toast.makeText(FoodBuilderActivity.this, text, Toast.LENGTH_LONG).show();
                    for(int k = 0; k< jsarray.length() ;k++)
                    {
                        try
                        {
                            if(jsarray.getJSONObject(k).getString("categ_name").equals(text))
                            {
                                list = new ArrayList<>();
                                get = new FoodBuilderActivity.GetFood();
                                offset = 0;
                                String args[] = new String[10];
                                args[0] = "http://caloriesdiary.ru/food/get_food";
                                args[1] = String.valueOf(offset);
                                args[2] = searchquery; //строка поиска
                                args[3] = jsarray.getJSONObject(k).getString("id"); //id категории
                                args[4] = ""; //сорт имени
                                args[5] = ""; //сорт ккал
                                args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
                                args[7] = FirebaseInstanceId.getInstance().getToken();
                                get.execute(args);
                            }
                        }
                        catch(Exception e2)
                        {

                        }


                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(FoodBuilderActivity.this, "Пустота", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
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
//        mAdapter = new RecycleFoodAdapter(initData());
//        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (totalItemCount - 1 == lastVisibleItems && send) {
                    buff = lastVisibleItems - visibleItemCount + 1;
                    get = new GetFood();
                    send = false;
                    offset += 1;

                    String args[] = new String[10];
                    args[0] = "http://caloriesdiary.ru/food/get_food";
                    args[1] = String.valueOf(offset);
                    args[2] = searchquery; //строка поиска
                    args[3] = ""; //id категории
                    args[4] = ""; //сорт имени
                    args[5] = ""; //сорт ккал
                    args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
                    args[7] = FirebaseInstanceId.getInstance().getToken();
                    get.execute(args);

                    //get.execute("http://caloriesdiary.ru/food/get_food", String.valueOf(offset));

                    Toast.makeText(FoodBuilderActivity.this, String.valueOf(buff), Toast.LENGTH_SHORT).show();
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

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
            }
        });

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
                    get = new GetFood();
                    send = false;
                    offset+=1;
                    get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(offset));

                    dialogAdapter = new CustomFoodAdapter(list);
                    dialogRecyclerView.setAdapter(dialogAdapter);
                    ((LinearLayoutManager)dialogRecyclerView.getLayoutManager()).scrollToPosition(buff);
                    Toast.makeText(FoodBuilderActivity.this, String.valueOf(buff), Toast.LENGTH_SHORT).show();
                }

            }
        });


        send = true;
//        get = new FoodBuilderActivity.GetFood();
//        get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(offset));

        dialogAdapter = new CustomFoodAdapter(list);
        dialogRecyclerView.setAdapter(dialogAdapter);

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
            post.execute(args);
            String ans = post.get().toString();
            Toast.makeText(this, ans, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {

        }



    }



    private class GetFood extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            //Toast.makeText(FoodBuilderActivity.this, "Загрузка блюд...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            send = true;
            JSONArray resp = null;
            String foodName = null;
            Float b=0f, j=0f, u=0f, calories=0f;
            Integer id=0;

            Integer food_id = 0;

            try {

                String answer = get.get();
                try
                {
                    Toast.makeText(FoodBuilderActivity.this, "ans = " + answer, Toast.LENGTH_SHORT).show();
                    resp = new JSONArray(answer);
                }
                catch (Exception e)
                {
                   //
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(FoodBuilderActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            if (resp != null)
                for(int i = 0; i<resp.length(); i++){
                    try {
                        id = Integer.valueOf(resp.getJSONObject(i).getString("category_id"));

                        food_id = Integer.valueOf(resp.getJSONObject(i).getString("food_id"));

                        foodName = resp.getJSONObject(i).getString("name");
                        b = Float.valueOf(resp.getJSONObject(i).getString("protein"));
                        j = Float.valueOf(resp.getJSONObject(i).getString("fats"));
                        u = Float.valueOf(resp.getJSONObject(i).getString("carbs"));
                        calories = Float.valueOf(resp.getJSONObject(i).getString("calories"));
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный формат строки!");
                    } catch (JSONException jEx){
                        Toast.makeText(getApplicationContext(),jEx.toString(), Toast.LENGTH_SHORT).show();
                        //errors.setText(jEx.toString());
                    }
                        list.add(new FoodItem(food_id,foodName,b,j,u,id,calories));

                }

            mAdapter = new RecycleFoodAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            Toast.makeText(FoodBuilderActivity.this, "Загружено элементов: " + String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
            ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPosition(buff);


        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL(strings[0]); // первый аргумент из массива который передан при вызове
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("offset",strings[1]);
                postDataParams.put("query",strings[2]);
                postDataParams.put("categ_id",strings[3]);
                postDataParams.put("sort_names",strings[4]);
                postDataParams.put("sort_calories",strings[5]);
                postDataParams.put("id", String.valueOf(sharedPref.getInt("PROFILE_ID",0)));
                postDataParams.put("instanceToken", FirebaseInstanceId.getInstance().getToken());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(150000 /* milliseconds */);
                conn.setConnectTimeout(150000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams)); // преобразуем json объект в строку параметров запроса

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream(), "UTF-8"));
                    //StringBuffer sb = new StringBuffer("");
                    String line;
                    try
                    {
                        JSONObject js = null;
                        while((line = in.readLine()) != null) {
                            js = new JSONObject(line);
                            break;
                        }
                        JSONArray jArr = js.getJSONArray("food");

                        in.close();
                        return jArr.toString();
                    }
                    catch (Exception e)
                    {
                        return "JSONparse error: " + e.toString();
                    }



                }
                else {
                    return "Response error: " + String.valueOf(responseCode);
                }
            }
            catch(Exception e){
                return "Connection error: " + e.toString();
            }

        }
        private String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);


                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }


}

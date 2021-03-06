package com.caloriesdiary.caloriesdiary.Activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Adapters.CustomFoodAdapter;
import com.caloriesdiary.caloriesdiary.Adapters.IngItemAdapter;
import com.caloriesdiary.caloriesdiary.HTTP.Get;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.Adapters.RecycleFoodAdapter;
import com.caloriesdiary.caloriesdiary.RecyclerTouchListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;


public class FoodBuilderActivity extends AppCompatActivity implements CallBackListener {

    JSONArray allFoodArray = new JSONArray();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView dialogRecyclerView;
    private RecyclerView.Adapter dialogAdapter;
    private RecyclerView.LayoutManager dialogLayoutManager;

    private TextView customCalories, customProt, customCarbs, customFats, clearIngList;
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
    boolean send = true, changescrollpos = false, updialog = false, canscroll=true;
    int buff=0;
    String searchquery = "",searchcatid = "",searchsortkcal = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.food_builder_layout);
            setTitle("Справочник блюд");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
            tabWidget.setElevation(getSupportActionBar().getElevation());
            getSupportActionBar().setElevation(0f);
            handleIntent(getIntent());
            initTabs();
            initObjects();
            initData();
            ingRecyclerTouchListener();

            ingAdapter = new IngItemAdapter(item);
            ingRecyclerView.setAdapter(ingAdapter);
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        customCalories = (TextView) findViewById(R.id.builder_food_calories_value);
        customProt = (TextView) findViewById(R.id.builder_food_protein_value);
        customCarbs = (TextView) findViewById(R.id.builder_food_carbs_value);
        customFats = (TextView) findViewById(R.id.builder_food_fats_value);
        clearIngList = (TextView) findViewById(R.id.clear_ing_list);
    }

    private void initData() {
        post = new Post();
        list = new ArrayList<>();
        String args[] = new String[8];
        args[0] = "http://caloriesdiary.ru/food/get_food";
        args[1] = String.valueOf(offset); //offset
        args[2] = searchquery; //query
        args[3] = ""; //categ_id
        args[4] = "1"; //sort_names
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
        send = true;
        offset++;
    }

    private void fillList() {
        try
        {
            JSONObject json = post.get();

            if(json.getString("status").equals("1"))
            {

                FoodItem foodItemtoAdd;
                JSONArray jarr = json.getJSONArray("food");

                if(jarr.length()<150)
                    canscroll = false;

                for(int i = 0; i < jarr.length(); i++)
                {
                    foodItemtoAdd = new FoodItem();

                    foodItemtoAdd.setId(Integer.valueOf(jarr.getJSONObject(i).getString("food_id")));
                    if(jarr.getJSONObject(i).getString("name").length()<20)
                        foodItemtoAdd.setName(jarr.getJSONObject(i).getString("name"));
                    else
                        foodItemtoAdd.setName(jarr.getJSONObject(i).getString("name").substring(0,18) + "...");
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
                    foodItemtoAdd.setB(Float.valueOf(customjarr.getJSONObject(i).getString("protein")));
                    foodItemtoAdd.setJ(Float.valueOf(customjarr.getJSONObject(i).getString("fats")));
                    foodItemtoAdd.setU(Float.valueOf(customjarr.getJSONObject(i).getString("carbs")));
                    foodItemtoAdd.setCalories(Float.valueOf(customjarr.getJSONObject(i).getString("calories")));
                    foodItemtoAdd.setCategoryId(0);
                    list.add(foodItemtoAdd);
                }

                for(int i=0; i<jarr.length(); i++){
                    allFoodArray.put(jarr.getJSONObject(i));
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

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    offset = 0;
                    list = new ArrayList<>();
                    post = new Post();
                    searchquery = "";
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
                    return false;
                }
            });

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
            offset = 0;
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
            offset = 0;
            get = new Get();
            get.execute("http://caloriesdiary.ru/food/get_food_categories");

            //Toast.makeText(this,"ans:" + get.get().toString(), Toast.LENGTH_LONG).show();
            final JSONObject json = get.get();
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
                        try {
                        spinner1.setEnabled(true);
                        String text = spinner1.getSelectedItem().toString();
                        final JSONArray jarr2 = json.getJSONArray("categories");
                        for (int k = 0; k < jarr2.length(); k++) {

                            if (jarr2.getJSONObject(k).getString("categ_name").equals(text) && buildercheck1.isChecked()) {
                                list = new ArrayList<>();
                                post = new Post();
                                offset = 0;
                                searchcatid = jarr2.getJSONObject(k).getString("id");
                                String args[] = new String[10];
                                args[0] = "http://caloriesdiary.ru/food/get_food";
                                args[1] = String.valueOf(offset);
                                args[2] = searchquery; //строка поиска
                                args[3] = searchcatid; //id категории
                                args[4] = "1"; //сорт имени
                                args[5] = searchsortkcal; //сорт ккал
                                args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                                args[7] = FirebaseInstanceId.getInstance().getToken();
                                post.setListener(listener);
                                post.execute(args);
                            }
                        }
                            } catch (Exception e2) {

                            }




                    }
                    else
                    {
                        spinner1.setEnabled(false);
                        searchcatid = "";
                        offset = 0;
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
                            offset = 0;
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
                            offset = 0;
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
                        offset = 0;
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
                            offset = 0;
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
                            offset = 0;
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
                           // Toast.makeText(FoodBuilderActivity.this, "kcal: " + searchsortkcal, Toast.LENGTH_SHORT).show();
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

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_food_busket_layout, null);

                final TextView txtName = view.findViewById(R.id.recycler_food_item_name);
                final TextView dialogName = content.findViewById(R.id.dialog_food_title);
                try {
                    dialogName.setText(allFoodArray.getJSONObject(position).getString("name"));
                }catch (Exception e){

                }
                final TextView txtBJU = view.findViewById(R.id.recycler_food_item_bju);
                final TextView txtCalories = view.findViewById(R.id.recycler_food_item_calories);
                final TextView dialogCalories = content.findViewById(R.id.dialog_cal);
                final TextView caloriesPerGr = content.findViewById(R.id.dialog_cal_per_gr);
                caloriesPerGr.setText(txtCalories.getText().toString() + "ккал/ 100г");
                dialogCalories.setText(txtCalories.getText().toString() + "ккал");
                final TextView dialogProteins = content.findViewById(R.id.dialog_protein_value);
                final TextView dialogFats = content.findViewById(R.id.dialog_fats_value);
                final TextView dialogCarbs = content.findViewById(R.id.dialog_carbs_value);

                AlertDialog.Builder builder = new AlertDialog.Builder(FoodBuilderActivity.this);
                builder.setCancelable(false);


                final EditText input = content.findViewById(R.id.dialog_gr);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);


                final double caloriesCount = Double.parseDouble(txtCalories.getText().toString().
                        substring(0, txtCalories.getText().toString().indexOf('.')));
                String parsBJU = txtBJU.getText().toString();


                final double dialogProteinValue = Double.parseDouble(parsBJU.
                        substring(0, parsBJU.indexOf('/')));
                dialogProteins.setText(String.valueOf(dialogProteinValue) + "г");

                parsBJU = parsBJU.substring(parsBJU.indexOf('/') + 1);
                final double dialogFatsValue = Double.parseDouble(parsBJU.
                        substring(0, parsBJU.indexOf('/')));
                dialogFats.setText(String.valueOf(dialogFatsValue) + "г");
                parsBJU = parsBJU.substring(parsBJU.indexOf('/') + 1);
                final double dialogCarbsValue = Double.parseDouble(parsBJU);
                dialogCarbs.setText(String.valueOf(dialogCarbsValue) + "г");

                final Button OKBtn = content.findViewById(R.id.add_food_to_busket);
                final Button cancelBtn = content.findViewById(R.id.cancel_dialog_food);
                input.setText("100");
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                        if (input.getText().length() > 0) {
                            double massCount = Double.parseDouble(input.getText().toString());
                            OKBtn.setClickable(true);
                            double newCount = Math.round(
                                    (massCount / 100) * caloriesCount);
                            dialogCalories.setText(String.valueOf(newCount)+" ккал");

                            double protein = (massCount / 100) * dialogProteinValue;
                            double fats = (massCount / 100) * dialogFatsValue;
                            double carbs = (massCount / 100) * dialogCarbsValue;

                            dialogProteins.setText(Math.round(protein*100.0)/100.0 + " г");
                            dialogFats.setText(Math.round(fats*100.0)/100.0 + " г");
                            dialogCarbs.setText(Math.round(carbs*100.0)/100.0 + " г");
                        } else {
                            OKBtn.setClickable(false);
                            dialogProteins.setText("");
                            dialogFats.setText("");
                            dialogCarbs.setText("");
                            dialogCalories.setText("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


                builder.setView(content);
                final AlertDialog alert = builder.create();
                alert.show();

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.cancel();
                    }
                });

                OKBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject jObject = new JSONObject();
                        try {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsn = new JSONObject();
                            File f = new File(getCacheDir(), "Food.txt");
                            if (f.exists()) {
                                FileInputStream in = new FileInputStream(f);
                                ObjectInputStream inObject = new ObjectInputStream(in);
                                String text = inObject.readObject().toString();
                                inObject.close();


                                jsn = new JSONObject(text);
                                jsonArray = jsn.getJSONArray("food");
                                jsn.remove("food");
                            }

                            jsn.put("name", txtName.getText().toString());
                            String s = dialogProteins.getText().toString();
                            jsn.put("protein", s.substring(0, s.indexOf('г')));
                            s = dialogFats.getText().toString();
                            jsn.put("fats", s.substring(0, s.indexOf('г')));
                            s = dialogCarbs.getText().toString();
                            jsn.put("carbs", s.substring(0, s.indexOf('г')));
                            jsn.put("calories", dialogCalories.getText().toString()
                                    .substring(0, dialogCalories.getText().toString().indexOf('.')));
                            jsonArray.put(jsn);
                            jObject.put("food", jsonArray);

                            FileOutputStream out = new FileOutputStream(f);
                            ObjectOutputStream outObject = new ObjectOutputStream(out);
                            outObject.writeObject(jObject.toString());
                            outObject.flush();
                            out.getFD().sync();
                            outObject.close();

                            alert.dismiss();

                        } catch (Exception iEx) {
                            Toast.makeText(getApplicationContext(), iEx.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (totalItemCount - 1 == lastVisibleItems && send && canscroll) {
                    buff = lastVisibleItems - visibleItemCount + 1;

                    send = false;

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

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        for(int i=0; i<tabWidget.getChildCount(); i++){
            final ViewGroup tab = (ViewGroup) tabWidget.getChildAt(i);
            final TextView tabTextView = (TextView) tab.getChildAt(1); // Magic number
            tabTextView.setTextColor(Color.WHITE);
        }
//        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            public void onTabChanged(String tabId) {
//                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void clearList(){
        while (!list.isEmpty()){
            list.remove(0);
        }
        updialog = true;
        send = true;
        offset = 0;
        canscroll = true;
    }

    private void customValues(){
        if(item.size()>0) {
            float calories = 0, carbs = 0, protein = 0, fats = 0, mass = 0;
            for (int i = 0; i < item.size(); i++) {
                calories += item.get(i).getCalories();
                carbs += item.get(i).getU();
                fats += item.get(i).getJ();
                protein += item.get(i).getB();
                mass += item.get(i).getMass();
            }

            calories = calories * 100 / mass ;
            carbs = carbs * 100 / mass;
            fats = fats * 100 / mass;
            protein = protein * 100 / mass;

            customCalories.setText(String.valueOf(Math.round(calories*10)/10.0));
            customFats.setText(String.valueOf(Math.round(fats*10)/10.0));
            customProt.setText(String.valueOf(Math.round(protein*10)/10.0));
            customCarbs.setText(String.valueOf(Math.round(carbs*10)/10.0));
        } else{
            customCalories.setText("0");
            customFats.setText("0");
            customProt.setText("0");
            customCarbs.setText("0");
        }
    }

    public void onAddFood (View view)
    {
        View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_food_dialoglayout,null);

        final EditText edittext =  content.findViewById(R.id.custom_food_search);

        dialogRecyclerView =  content.findViewById(R.id.custom_food_recycle);
        dialogRecyclerView.setHasFixedSize(true);

        dialogLayoutManager = new LinearLayoutManager(this);
        dialogRecyclerView.setLayoutManager(dialogLayoutManager);

        clearIngList.setOnClickListener(new View.OnClickListener() {
                        @Override
            public void onClick(View view) {
                            while(!item.isEmpty())
                                item.remove(0);
                            customValues();
                            ingAdapter.notifyDataSetChanged();
            }
        });

        clearList();
        post = new Post();
        String args[] = new String[8];
        args[0] = "http://caloriesdiary.ru/food/get_food";
        args[1] = String.valueOf(offset); //offset
        args[2] = ""; //query
        args[3] = ""; //categ_id
        args[4] = "1"; //sort_names
        args[5] = ""; //sort_calories
        args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
        args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
        post.setListener(listener);
        post.execute(args);

        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                clearList();
                post = new Post();
                String args[] = new String[8];
                args[0] = "http://caloriesdiary.ru/food/get_food";
                args[1] = String.valueOf(offset); //offset
                args[2] = edittext.getText().toString(); //query
                args[3] = ""; //categ_id
                args[4] = "1"; //sort_names
                args[5] = ""; //sort_calories
                args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                post.setListener(listener);
                post.execute(args);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialogRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = dialogLayoutManager.getChildCount();
                int totalItemCount = dialogLayoutManager.getItemCount();
                int lastVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                //edittext.setText(String.valueOf(offset)+":"+String.valueOf(visibleItemCount) + ":" +String.valueOf(totalItemCount) + ":"+String.valueOf(lastVisibleItems));
                if (totalItemCount -1 == lastVisibleItems && send && canscroll){
                    buff = lastVisibleItems-visibleItemCount+1;
                    //get = new GetFood();
                    send = false;
                    updialog = true;
                    post = new Post();
                    String args[] = new String[8];
                    args[0] = "http://caloriesdiary.ru/food/get_food";
                    args[1] = String.valueOf(offset); //offset
                    args[2] = edittext.getText().toString(); //query
                    args[3] = ""; //categ_id
                    args[4] = "1"; //sort_names
                    args[5] = ""; //sort_calories
                    args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                    args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                    post.setListener(listener);
                    post.execute(args);
                    try {
                        wait(100);
                    } catch (Exception e){}
                    (dialogRecyclerView.getLayoutManager()).scrollToPosition(buff);
                    //Toast.makeText(FoodBuilderActivity.this, String.valueOf(buff), Toast.LENGTH_SHORT).show();
                }

            }
        });


        final Button okBtn = content.findViewById(R.id.add_ingr);
        final TextView cancelDialog = content.findViewById(R.id.cancel_builder);


        dialogRecyclerView.addOnItemTouchListener( new RecyclerTouchListener(this, dialogRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final CheckBox checkbox = view.findViewById(R.id.add_food_checkbox);
                final TextView textB = view.findViewById(R.id.viewB);
                final TextView textJ = view.findViewById(R.id.viewJ);
                final TextView textU = view.findViewById(R.id.viewU);
                final TextView textKc = view.findViewById(R.id.viewKc);
                final TextView textId = view.findViewById(R.id.viewID);


                if(!checkbox.isChecked())
                {
                  //  Toast.makeText(FoodBuilderActivity.this, "Добавить " + checkbox.getText().toString() , Toast.LENGTH_SHORT).show();
                    item.add(new FoodItem(Integer.valueOf(textId.getText().toString()),checkbox.getText().toString(),Float.valueOf(textB.getText().toString()),Float.valueOf(textJ.getText().toString()),Float.valueOf(textU.getText().toString()),0,Float.valueOf(textKc.getText().toString()),100));

                }
                else
                {
                   // Toast.makeText(FoodBuilderActivity.this,"Удалить " + checkbox.getText().toString(), Toast.LENGTH_SHORT).show();
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
                builder.setView(content);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customValues();
                clearList();
                post = new Post();
                String args[] = new String[8];
                args[0] = "http://caloriesdiary.ru/food/get_food";
                args[1] = String.valueOf(offset); //offset
                args[2] = ""; //query
                args[3] = ""; //categ_id
                args[4] = "1"; //sort_names
                args[5] = ""; //sort_calories
                args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                post.setListener(listener);
                post.execute(args);

                alertDialog.cancel();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ingAdapter.notifyDataSetChanged();
                clearList();
                customValues();
                post = new Post();
                String args[] = new String[8];
                args[0] = "http://caloriesdiary.ru/food/get_food";
                args[1] = String.valueOf(offset); //offset
                args[2] = ""; //query
                args[3] = ""; //categ_id
                args[4] = "1"; //sort_names
                args[5] = ""; //sort_calories
                args[6] = String.valueOf(sharedPref.getInt("PROFILE_ID",0)); //id
                args[7] = FirebaseInstanceId.getInstance().getToken(); //instanceToken
                post.setListener(listener);
                post.execute(args);

                alertDialog.dismiss();
            }
        });



    }



    private void ingRecyclerTouchListener(){
        ingRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, ingRecyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final EditText mass = view.findViewById(R.id.recycler_ing_item_mass);
                final FoodItem foodItem = item.get(position);
                mass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (!mass.getText().toString().equals("")) {
                            foodItem.setCalories(foodItem.getCalories() / Float.valueOf(foodItem.getMass()));
                            foodItem.setB(foodItem.getB() / Float.valueOf(foodItem.getMass()));
                            foodItem.setJ(foodItem.getJ() / Float.valueOf(foodItem.getMass()));
                            foodItem.setU(foodItem.getU() / Float.valueOf(foodItem.getMass()));

                            foodItem.setCalories(foodItem.getCalories() * Float.valueOf(mass.getText().toString()));
                            foodItem.setB(foodItem.getB() * Float.valueOf(mass.getText().toString()));
                            foodItem.setJ(foodItem.getJ() * Float.valueOf(mass.getText().toString()));
                            foodItem.setU(foodItem.getU() * Float.valueOf(mass.getText().toString()));
                            foodItem.setMass(Integer.valueOf(mass.getText().toString()));
                        }
                        customValues();
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {
                item.remove(position);
                customValues();
                //Toast.makeText(FoodBuilderActivity.this, "оно удалилось", Toast.LENGTH_SHORT).show();
                ingAdapter.notifyDataSetChanged();
            }
        }));
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

            String args[] = new String[8];
            args[0] = "http://caloriesdiary.ru/food/save_custom_dish";
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[2] = nameedit.getText().toString();
            args[3] = jsn.toString();
            args[4] = customCalories.getText().toString();
            args[5] = customProt.getText().toString();
            args[6] = customFats.getText().toString();
            args[7] = customCarbs.getText().toString();
            post.setListener(listener);
            post.execute(args);
            String ans = post.get().toString();
            Toast.makeText(this, ans, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }



    }

}

package com.caloriesdiary.caloriesdiary.Activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Adapters.RecycleActionAdapter;
import com.caloriesdiary.caloriesdiary.HTTP.GetActions;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.Items.ActionItem;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RecycleActionCatalogActivity extends AppCompatActivity implements CallBackListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnActionsSortName;
    private boolean sortdir = true, sortkcal = true;
    Spinner spinner1;
    CheckBox checkbox1;
    ProgressBar progressbar1;
    CallBackListener listener;
    Post post;
    String query = "";
    List<ActionItem> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_action_catalog_layout);
        handleIntent(getIntent());
        setTitle("АКТИВНОСТЬ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);

        mRecyclerView =  (RecyclerView) findViewById(R.id.action_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        mAdapter = new RecycleActionAdapter(initData2());
//        mRecyclerView.setAdapter(mAdapter);

        addRecyclerClickListener();



        progressbar1 = (ProgressBar) findViewById(R.id.activity_progress_bar);

        initFiltres();
        initData();
    }

    private void initData() {
        post = new Post();
        String args[] = new String[4];
        args[0] = "http://caloriesdiary.ru/activities/get_activities";
        args[1] = ""; //query
        args[2] = "1"; //sort_names
        args[3] = ""; //sort_calories
        listener = this;
        post.setListener(listener);
        progressbar1.setVisibility(View.VISIBLE);
        post.execute(args);
    }

    private void filllist() {
        try
        {

            JSONObject json = post.get();
            list = new ArrayList<>();
            //Toast.makeText(this, json.toString(), Toast.LENGTH_SHORT).show();
            if(json.getString("status").equals("1"))
            {
                JSONArray jarr = json.getJSONArray("activities");
                for(int i = 0; i < jarr.length(); i++)
                {
                    list.add(new ActionItem(jarr.getJSONObject(i).getString("name"),Float.valueOf(jarr.getJSONObject(i).getString("calories")),Integer.valueOf(jarr.getJSONObject(i).getString("id"))));
                }
                mAdapter = new RecycleActionAdapter(list);
                mRecyclerView.setAdapter(mAdapter);
                progressbar1.setVisibility(View.GONE);
            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void preparePost()
    {
        post.setListener(listener);
        progressbar1.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        if(getIntent().getStringExtra(SearchManager.QUERY)==null)
        {
            query = "";
        }

        mAdapter = new RecycleActionAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initFiltres() {
        spinner1 = (Spinner) findViewById(R.id.activity_spinner1);
        checkbox1 = (CheckBox) findViewById(R.id.activity_checkbox1);
        String values[] = {"По возрастанию", "По убыванию"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        spinner1.setAdapter(adapter);

        if (!checkbox1.isChecked())
        {
            spinner1.setEnabled(false);
        }

        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    spinner1.setEnabled(true);
                    if(spinner1.getSelectedItemPosition()==0)
                    {
                        post = new Post();
                        String args[] = new String[4];
                        args[0] = "http://caloriesdiary.ru/activities/get_activities";
                        args[1] = query; //query
                        args[2] = "1"; //sort_names
                        args[3] = "1"; //sort_calories
                        preparePost();
                        post.execute(args);

                    }
                    else
                    {
                        post = new Post();
                        String args[] = new String[4];
                        args[0] = "http://caloriesdiary.ru/activities/get_activities";
                        args[1] = query; //query
                        args[2] = "1"; //sort_names
                        args[3] = "2"; //sort_calories
                        preparePost();
                        post.execute(args);
                    }


                }
                else
                {
                    spinner1.setEnabled(false);
                    post = new Post();
                    String args[] = new String[4];
                    args[0] = "http://caloriesdiary.ru/activities/get_activities";
                    args[1] = query; //query
                    args[2] = "1"; //sort_names
                    args[3] = ""; //sort_calories
                    preparePost();
                    post.execute(args);

                }

            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(spinner1.isEnabled())
                {

                    if(spinner1.getSelectedItemPosition()==0)
                    {
                        post = new Post();
                        String args[] = new String[4];
                        args[0] = "http://caloriesdiary.ru/activities/get_activities";
                        args[1] = query; //query
                        args[2] = ""; //sort_names
                        args[3] = "1"; //sort_calories
                        preparePost();
                        post.execute(args);

                    }
                    else
                    {
                        post = new Post();
                        String args[] = new String[4];
                        args[0] = "http://caloriesdiary.ru/activities/get_activities";
                        args[1] = query; //query
                        args[2] = ""; //sort_names
                        args[3] = "2"; //sort_calories
                        preparePost();
                        post.execute(args);
                    }



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void addRecyclerClickListener(){
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                final View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_action_busket_layout, null);

                final TextView txtName = view.findViewById(R.id.recycler_action_item_name);
                final TextView dialogName = content.findViewById(R.id.dialog_title);
                dialogName.setText(txtName.getText());
                final TextView dialogCalories = content.findViewById(R.id.dialog_burn_per_hour);
                final TextView txtCalories = view.findViewById(R.id.recycler_action_item_calories);

                String kcalstr = txtCalories.getText().toString().substring(0, txtCalories.getText().toString().indexOf('.'));
                String s = kcalstr + "ккал/час";
                dialogCalories.setText(s);
                final double kcal = Double.parseDouble(kcalstr);
                final TextView kcaltextview = content.findViewById(R.id.dialog_burn);

                final Button cancelBtn = content.findViewById(R.id.cancel_dialog);
                final Button OKBtn = content.findViewById(R.id.add_action_to_busket);


                final AlertDialog.Builder builder = new AlertDialog.Builder(RecycleActionCatalogActivity.this);


                final EditText input = content.findViewById(R.id.dialog_time);

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        double newkcal = kcal;
                        if (input.getText().length() > 0) {
                            OKBtn.setClickable(true);
                            int time = Integer.parseInt(input.getText().toString());
                            if (time < 1440) {
                                newkcal = Math.round(kcal * time / 60);
                                //Toast.makeText(getApplicationContext(),String.valueOf(newkcal),Toast.LENGTH_LONG).show();
                                kcaltextview.setText(newkcal + " ккал");
                            } else {
                                Toast.makeText(getApplicationContext(), "Не пизди", Toast.LENGTH_LONG).show();
                                input.setText("");
                            }

                        } else {

                            kcaltextview.setText("");
                            OKBtn.setClickable(false);
                        }

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

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
                            File f = new File(getCacheDir(), "Actions.txt");
                            if (f.exists()) {
                                FileInputStream in = new FileInputStream(f);
                                ObjectInputStream inObject = new ObjectInputStream(in);
                                String text = inObject.readObject().toString();
                                inObject.close();


                                jsn = new JSONObject(text);
                                jsonArray = jsn.getJSONArray("active");
                                jsn.remove("active");
                            }

                            jsn.put("name", dialogName.getText().toString());
                            jsn.put("calories", kcaltextview.getText().toString()
                                    .substring(0, kcaltextview.getText().toString().indexOf('.')));
                            jsonArray.put(jsn);
                            jObject.put("active", jsonArray);

                            FileOutputStream out = new FileOutputStream(f);
                            ObjectOutputStream outObject = new ObjectOutputStream(out);
                            outObject.writeObject(jObject.toString());
                            outObject.flush();
                            out.getFD().sync();
                            outObject.close();

                            alert.dismiss();
                            //Toast.makeText(getApplicationContext(), jObject.toString() , Toast.LENGTH_LONG).show();
                        } catch (Exception iEx) {
                            Toast.makeText(getApplicationContext(), iEx.toString(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try
        {
            MenuInflater inflater = getMenuInflater();

            inflater.inflate(R.menu.menu_main, menu);

            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {


        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
           // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            post = new Post();
            String args[] = new String[4];
            args[0] = "http://caloriesdiary.ru/activities/get_activities";
            args[1] = query; //query
            args[2] = "1"; //sort_names
            args[3] = ""; //sort_calories
            preparePost();
            post.execute(args);
        }
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
    public void callback() {


        filllist();
    }
}


package com.caloriesdiary.caloriesdiary;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class RecycleActionCatalogActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String query;
    private Button btnActionsSortName,btnActionsSortKcal;
    private boolean sortdir = true, sortkcal = true;
    List<ActionItem> list = new ArrayList<ActionItem>();

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

        mAdapter = new RecycleActionAdapter(initData());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                TextView textView = (TextView) view.findViewById(R.id.recycler_action_item_name);
                //Values are passing to activity & to fragment as well
//                Toast.makeText(getApplicationContext(), "Single Click on position :"+position + " " + textView.getText().toString(),
//                        Toast.LENGTH_SHORT).show();

                JSONObject jsn = new JSONObject();
                final TextView txtName = (TextView) view.findViewById(R.id.recycler_action_item_name);
                final TextView txtCalories = (TextView) view.findViewById(R.id.recycler_action_item_calories);
                String kcalstr = txtCalories.getText().toString().substring(0, txtCalories.getText().toString().indexOf('.'));
                final double kcal = Double.parseDouble(kcalstr);
                final TextView kcaltextview = new TextView(RecycleActionCatalogActivity.this);
                kcaltextview.setText(kcalstr + " kcal");
                final AlertDialog.Builder builder = new AlertDialog.Builder(RecycleActionCatalogActivity.this);
                builder.setTitle(txtName.getText().toString())
                        .setCancelable(false)
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (((LinearLayout) lp).getChildCount() > 0)
                                            ((LinearLayout) lp).removeAllViews();
                                        ((ViewManager) lp.getParent()).removeView(lp);

                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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

                                            jsn.put("name", txtName.getText().toString());
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

                                            //Toast.makeText(getApplicationContext(), jObject.toString() , Toast.LENGTH_LONG).show();
                                        } catch (Exception iEx) {
                                            Toast.makeText(getApplicationContext(), iEx.toString(), Toast.LENGTH_LONG).show();


                                        }

                                        if (((LinearLayout) lp).getChildCount() > 0)
                                            ((LinearLayout) lp).removeAllViews();
                                        ((ViewManager) lp.getParent()).removeView(lp);

                                    }
                                });

                final EditText input = new EditText(RecycleActionCatalogActivity.this);
                input.setHint("Введите время (в минутах)");

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        double newkcal = kcal;
                        if (input.getText().length() > 0) {
                            int time = Integer.parseInt(input.getText().toString());
                            if (time < 1440) {
                                newkcal = kcal * time / 60;
                                //Toast.makeText(getApplicationContext(),String.valueOf(newkcal),Toast.LENGTH_LONG).show();
                                kcaltextview.setText(newkcal + " kcal");
                            } else {
                                Toast.makeText(getApplicationContext(), "Не пизди", Toast.LENGTH_LONG).show();
                                input.setText("");
                            }

                        } else {
                            kcaltextview.setText(newkcal + " kcal");

                        }

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                lp.addView(kcaltextview, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(input, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                builder.setView(lp);
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        btnActionsSortName = (Button) findViewById(R.id.btnActionsSortName);
        btnActionsSortKcal = (Button) findViewById(R.id.btnActionsSortKcal);
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
            String query = intent.getStringExtra(SearchManager.QUERY);
           // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            List<ActionItem> querylist = new ArrayList<ActionItem>();
            for(int i = 0; i < list.size() ; i++ )
            {
                ActionItem item = list.get(i);
             if(item.getName().contains(query))
             {
                 querylist.add(item);
             }
            }

            mAdapter = new RecycleActionAdapter(querylist);
            mRecyclerView.setAdapter(mAdapter);
            Toast.makeText(getApplicationContext(), "Найдено элементов: " + String.valueOf(querylist.size() ), Toast.LENGTH_SHORT).show();
            //use the query to search
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh:
                mAdapter = new RecycleActionAdapter(list);
                mRecyclerView.setAdapter(mAdapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickActionsSortName(View view)
    {
        if (sortdir) {
            list.sort(new Comparator<ActionItem>() {
                @Override
                public int compare(ActionItem actionItem, ActionItem t1) {
                    return actionItem.getName().compareTo(t1.getName());
                }
            });
            mAdapter = new RecycleActionAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            btnActionsSortName.setText("Сортировать (Я-А)");
            sortdir = false;
        }
        else
        {
            list.sort(new Comparator<ActionItem>() {
                @Override
                public int compare(ActionItem actionItem, ActionItem t1) {
                    return t1.getName().compareTo(actionItem.getName());
                }
            });
            mAdapter = new RecycleActionAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            btnActionsSortName.setText("Сортировать (А-Я)");
            sortdir = true;
        }
    }
    public void onClickActionsSortKcal(View view)
    {
        if(sortkcal)
        {
            list.sort(new Comparator<ActionItem>() {
                @Override
                public int compare(ActionItem actionItem, ActionItem t1) {
                    return actionItem.getCalories().compareTo(t1.getCalories());
                }
            });
            mAdapter = new RecycleActionAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            sortkcal = false;
        }
        else
        {
            list.sort(new Comparator<ActionItem>() {
                @Override
                public int compare(ActionItem actionItem, ActionItem t1) {
                    return t1.getCalories().compareTo(actionItem.getCalories());
                }
            });
            mAdapter = new RecycleActionAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            sortkcal = true;
        }

    }
    public  String getAction() throws InterruptedException, ExecutionException {
        GetActions get = new GetActions();
        get.execute("http://94.130.12.179/activities/get_activities");

        return get.get().toString();
    }

    private List<ActionItem> initData() {

        String resp = null;
        String actionName;
        Float calories=new Float(0);
        Integer id=0;

        try {
            resp = getAction();
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

                actionName = resp.substring(0,resp.indexOf(','));
                resp=resp.substring(resp.indexOf(',')+1);

                try {
                    Float f1 = new Float(resp.substring(0,resp.indexOf(';')));
                    calories=f1;
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат строки!");
                }

                resp=resp.substring(resp.indexOf(';')+1);

                if(calories!=0)
                    list.add(new ActionItem(actionName, calories, id));
            }

        return list;
    }
}


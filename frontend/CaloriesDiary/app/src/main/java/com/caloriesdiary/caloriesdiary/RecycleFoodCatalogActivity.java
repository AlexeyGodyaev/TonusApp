package com.caloriesdiary.caloriesdiary;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RecycleFoodCatalogActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<FoodItem> list = new ArrayList<FoodItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_food_catalog_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        handleIntent(getIntent());
        setTitle("СПРАВОЧНИК БЛЮД");

        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);

        mRecyclerView =  (RecyclerView) findViewById(R.id.food_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecycleFoodAdapter(initData());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final TextView txtName = (TextView) view.findViewById(R.id.recycler_food_item_name);
                TextView txtBJU = (TextView) view.findViewById(R.id.recycler_food_item_bju);
                final TextView txtCalories = (TextView) view.findViewById(R.id.recycler_food_item_calories);
                final TextView dialogBJU = new TextView(RecycleFoodCatalogActivity.this);
                final TextView dialogCalories = new TextView(RecycleFoodCatalogActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(RecycleFoodCatalogActivity.this);
                builder.setTitle(txtName.getText())
                        .setCancelable(false)
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (lp.getChildCount() > 0)
                                            lp.removeAllViews();
                                        ((ViewManager) lp.getParent()).removeView(lp);
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("OK",
                                new
                                        DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                JSONObject jObject = new JSONObject();
                                                try {
                                                    JSONArray jsonArray = new JSONArray();
                                                    JSONObject jsn=new JSONObject();
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
                                                    String s = dialogBJU.getText().toString();
                                                    jsn.put("protein", s.substring(0, s.indexOf('/')));
                                                    s = s.substring(s.indexOf('/') + 1);
                                                    jsn.put("fats", s.substring(0, s.indexOf('/')));
                                                    s = s.substring(s.indexOf('/') + 1);
                                                    jsn.put("carbs", s);
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

                                                }
                                                catch (Exception iEx){
                                                    Toast.makeText(getApplicationContext(), iEx.toString() , Toast.LENGTH_LONG).show();

                                                }
                                                if (lp.getChildCount() > 0)
                                                    lp.removeAllViews();
                                                ((ViewManager) lp.getParent()).removeView(lp);
                                            }
                                        });


                final EditText input = new EditText(RecycleFoodCatalogActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                dialogBJU.setText(txtBJU.getText().toString());
                dialogCalories.setText(txtCalories.getText().toString());

                lp.addView(dialogCalories, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(dialogBJU, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(input, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                final double caloriesCount = Double.parseDouble(txtCalories.getText().toString().
                        substring(0,dialogCalories.getText().toString().indexOf('.')));
                String parsBJU = txtBJU.getText().toString();
                final double dialogProtein = Double.parseDouble(parsBJU.
                        substring(0, parsBJU.indexOf('/')));
                parsBJU = parsBJU.substring(parsBJU.indexOf('/')+1);
                final double dialogFats = Double.parseDouble(parsBJU.
                        substring(0,parsBJU.indexOf('/')));
                parsBJU = parsBJU.substring(parsBJU.indexOf('/')+1);
                final double dialogCarbs = Double.parseDouble(parsBJU);

                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        double massCount = Double.parseDouble(input.getText().toString());

                        double newCount =
                                (massCount/100)*caloriesCount;
                        dialogCalories.setText(Double.toString(newCount));

                        double protein = (massCount/100)*dialogProtein;
                        double fats = (massCount/100)*dialogFats;
                        double carbs = (massCount/100)*dialogCarbs;

                        dialogBJU.setText(Double.toString(protein)+"/"+Double.toString(fats)+"/"+Double.toString(carbs));

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                builder.setView(lp);
                AlertDialog alert = builder.create();
                alert.show();

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
               // int firstVisibleItems = mLayoutManager.find
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        InitList initList = new InitList();
        initList.execute();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh:
                mAdapter = new RecycleFoodAdapter(initData());
                mRecyclerView.setAdapter(mAdapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            List<FoodItem> querylist = new ArrayList<FoodItem>();
            for(int i = 0; i < list.size() ; i++ )
            {
                FoodItem item = list.get(i);
                if(item.getName().contains(query))
                {
                    querylist.add(item);
                }
            }

            mAdapter = new RecycleFoodAdapter(querylist);
            mRecyclerView.setAdapter(mAdapter);
            //use the query to search
        }
    }

    public JSONArray getFood() throws InterruptedException,
            ExecutionException {
        GetFood get = new GetFood();
        get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(-1));

        return get.get();
    }

    private List<FoodItem> initData() {

        JSONArray resp = null;
        String foodName = null;
        Float b=new Float(0), j=new Float(0), u=new Float(0), calories=new Float(0);
        Integer id=0;

        try {
            resp = getFood();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {
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
 class InitList extends AsyncTask<Void,Void,Void>
 {
     @Override
     protected void onPreExecute() {
         super.onPreExecute();
         Toast.makeText(RecycleFoodCatalogActivity.this, "Начало", Toast.LENGTH_SHORT).show();
     }

     @Override
     protected Void doInBackground(Void... voids) {
         //Toast.makeText(RecycleFoodCatalogActivity.this, "Этап1", Toast.LENGTH_SHORT).show();
         try
         {

         }
         catch (Exception e)
         {

         }
         //Toast.makeText(RecycleFoodCatalogActivity.this, "Этап2", Toast.LENGTH_SHORT).show();
         return null;
     }

     @Override
     protected void onPostExecute(Void aVoid) {
         super.onPostExecute(aVoid);
         Toast.makeText(RecycleFoodCatalogActivity.this, "Конец", Toast.LENGTH_SHORT).show();
     }
 }
}

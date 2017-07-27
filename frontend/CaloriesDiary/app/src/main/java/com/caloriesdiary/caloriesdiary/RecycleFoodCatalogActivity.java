package com.caloriesdiary.caloriesdiary;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class RecycleFoodCatalogActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<FoodItem> list = new ArrayList<>();
    GetFood get;
    TextView errors;


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

        errors = (TextView) findViewById(R.id.error_view);
//        mAdapter = new RecycleFoodAdapter(initData());
//        mRecyclerView.setAdapter(mAdapter);

        get = new GetFood();
        get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(-1));



        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final TextView txtName = view.findViewById(R.id.recycler_food_item_name);
                TextView txtBJU = view.findViewById(R.id.recycler_food_item_bju);
                final TextView txtCalories = view.findViewById(R.id.recycler_food_item_calories);
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
                        dialogCalories.setText(String.valueOf(newCount));

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
                
                int firstVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                errors.setText(String.valueOf(visibleItemCount)+" "+String.valueOf(totalItemCount)+" "+String.valueOf(firstVisibleItems));
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
//                mAdapter = new RecycleFoodAdapter(initData());
//                mRecyclerView.setAdapter(mAdapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClickFoodBuilder(View view)
    {
            Intent intent = new Intent(getApplicationContext(),FoodBuilderActivity.class);
            startActivity(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            List<FoodItem> querylist = new ArrayList<>();
            for(int i = 0; i < list.size() ; i++ )
            {
                FoodItem item = list.get(i);
                if(item.getName().toLowerCase().contains(query.toLowerCase()))
                {
                    querylist.add(item);
                }
            }

            mAdapter = new RecycleFoodAdapter(querylist);
            mRecyclerView.setAdapter(mAdapter);
            //use the query to search
        }
    }

    private class GetFood extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            Toast.makeText(RecycleFoodCatalogActivity.this, "Загрузка блюд...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            JSONArray resp = null;
            String foodName = null;
            Float b=0f, j=0f, u=0f, calories=0f;
            Integer id=0;

            try {

                String answer = get.get();
                try
                {
                    resp = new JSONArray(answer);
                }
                catch (Exception e)
                {
                    errors.setText(get.get());
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(RecycleFoodCatalogActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            if (resp != null)
                for(int i = 0; i<resp.length(); i++){
                    try {
                        id = Integer.valueOf(resp.getJSONObject(i).getString("category_id"));
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
                    if(b!=0||j!=0||u!=0||calories!=0)
                        list.add(new FoodItem(foodName,b,j,u,id,calories));
                }

            mAdapter = new RecycleFoodAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            Toast.makeText(RecycleFoodCatalogActivity.this, "Загружено элементов: " + String.valueOf(list.size()), Toast.LENGTH_SHORT).show();


        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL(strings[0]); // первый аргумент из массива который передан при вызове
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("offset",strings[1]);

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
                        if((line = in.readLine()) != null) {
                            js = new JSONObject(line);
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

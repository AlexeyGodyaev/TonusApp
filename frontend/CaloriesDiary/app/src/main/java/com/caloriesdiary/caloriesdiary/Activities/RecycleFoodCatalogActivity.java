package com.caloriesdiary.caloriesdiary.Activities;


import android.app.SearchManager;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.Adapters.RecycleFoodAdapter;
import com.caloriesdiary.caloriesdiary.RecyclerTouchListener;

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
    int offset = 0;
    boolean send = true;
    int buff=0;

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

        get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(offset));



        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_food_busket_layout, null);

                final TextView txtName = view.findViewById(R.id.recycler_food_item_name);
                final TextView dialogName = content.findViewById(R.id.dialog_food_title);
                if(txtName.getText().toString().length()>20)
                    dialogName.setText(txtName.getText().toString().substring(0, 20) + "...");
                 else dialogName.setText(txtName.getText());
                final TextView txtBJU = view.findViewById(R.id.recycler_food_item_bju);
                final TextView txtCalories = view.findViewById(R.id.recycler_food_item_calories);
                final TextView dialogCalories = content.findViewById(R.id.dialog_cal);
                final TextView caloriesPerGr = content.findViewById(R.id.dialog_cal_per_gr);
                caloriesPerGr.setText(txtCalories.getText().toString() + "ккал/ 100г");
                dialogCalories.setText(txtCalories.getText().toString() + "ккал");
                final TextView dialogProteins = content.findViewById(R.id.dialog_protein_value);
                final TextView dialogFats = content.findViewById(R.id.dialog_fats_value);
                final TextView dialogCarbs = content.findViewById(R.id.dialog_carbs_value);

                AlertDialog.Builder builder = new AlertDialog.Builder(RecycleFoodCatalogActivity.this);
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

                int lastVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (totalItemCount -1 == lastVisibleItems && send){
                    buff = lastVisibleItems-visibleItemCount+1;
                    get = new GetFood();
                    send = false;
                    offset+=1;
                    get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(offset));

                    Toast.makeText(RecycleFoodCatalogActivity.this, String.valueOf(buff), Toast.LENGTH_SHORT).show();
                }

                errors.setText(String.valueOf(visibleItemCount)+" "+String.valueOf(totalItemCount)+" "+String.valueOf(lastVisibleItems));
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

            send = true;
            JSONArray resp = null;
            String foodName = null;
            Float b=0f, j=0f, u=0f, calories=0f;
            Integer id=0, category_id=0;

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
                        id = Integer.valueOf(resp.getJSONObject(i).getString("food_id"));
                        category_id = Integer.valueOf(resp.getJSONObject(i).getString("category_id"));
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
                        list.add(new FoodItem(id,foodName,b,j,u,category_id,calories));
                }

            mAdapter = new RecycleFoodAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
            Toast.makeText(RecycleFoodCatalogActivity.this, "Загружено элементов: " + String.valueOf(list.size()), Toast.LENGTH_SHORT).show();

            mRecyclerView.getLayoutManager().scrollToPosition(buff);
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

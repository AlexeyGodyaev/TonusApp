package com.caloriesdiary.caloriesdiary;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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


public class FoodBuilderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView dialogRecyclerView;
    private RecyclerView.Adapter dialogAdapter;
    private RecyclerView.LayoutManager dialogLayoutManager;

    List<FoodItem> list = new ArrayList<>();
    GetFood get;
    int offset = 0;
    boolean send = true;
    int buff=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_builder_layout);
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

        mRecyclerView =  (RecyclerView) findViewById(R.id.food_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        mAdapter = new RecycleFoodAdapter(initData());
//        mRecyclerView.setAdapter(mAdapter);

        get = new FoodBuilderActivity.GetFood();
        get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(offset));

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

                    Toast.makeText(FoodBuilderActivity.this, String.valueOf(buff), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    public void onAddFood (View view)
    {
        View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_food_dialoglayout,null);
        final EditText edittext = (EditText) content.findViewById(R.id.custom_food_search);


        dialogRecyclerView = (RecyclerView) content.findViewById(R.id.custom_food_recycle);
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

                if(!checkbox.isChecked())
                {
                    Toast.makeText(FoodBuilderActivity.this, "Добавить " + checkbox.getText().toString() + "(" + textB.getText().toString() + textJ.getText().toString() + textU.getText().toString()+ ")", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(FoodBuilderActivity.this,"Удалить " + checkbox.getText().toString(), Toast.LENGTH_SHORT).show();
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

    private class GetFood extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(FoodBuilderActivity.this, "Загрузка блюд...", Toast.LENGTH_SHORT).show();
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
                    if(b!=0||j!=0||u!=0||calories!=0)
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

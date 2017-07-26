package com.caloriesdiary.caloriesdiary;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FoodCatalogActivity extends FragmentActivity {
    boolean flag = true;
    ListView listView;
    EditText srch;
    private FilterFragment filterFragment;
    List<FoodItem> list;
    int offset;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_catalog_layout);

        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);

        filterFragment = new FilterFragment();
        manager = getSupportFragmentManager();

        srch = (EditText) findViewById(R.id.srchFood);
        srch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (i == KeyEvent.KEYCODE_ENTER)){
//высылаем пост с именем
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.foodList);
        list = new ArrayList<FoodItem>();
        offset = 0;
        final FoodAdapter adapter = new FoodAdapter(this, initData());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                srch.setText("i= " + String.valueOf(i) + ";" + "i1= "+ String.valueOf(i1)+ ";" + "i2= "+ String.valueOf(i2) );
                if(i + i1 >= i2) {
                    Toast.makeText(getApplicationContext(), "Загрузка следующей страницы...", Toast.LENGTH_SHORT).show();
                    offset++;
                    int buf = i;
                    initData();
                    listView.setAdapter(adapter);
                    listView.setSelection(buf);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                final TextView txtName = (TextView) view.findViewById(R.id.productName);
                TextView txtBJU = (TextView) view.findViewById(R.id.bJU);
                final TextView txtCalories = (TextView) view.findViewById(R.id.productCalories);
                final TextView dialogBJU = new TextView(FoodCatalogActivity.this);
                final TextView dialogCalories = new TextView(FoodCatalogActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(FoodCatalogActivity.this);
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

// Toast.makeText(getApplicationContext(), jObject.toString() , Toast.LENGTH_LONG).show();
                                                }
                                                catch (Exception iEx){
                                                    Toast.makeText(getApplicationContext(), iEx.toString() , Toast.LENGTH_LONG).show();

                                                }
                                                if (lp.getChildCount() > 0)
                                                    lp.removeAllViews();
                                                ((ViewManager) lp.getParent()).removeView(lp);
                                            }
                                        });


                final EditText input = new EditText(FoodCatalogActivity.this);

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
        });
    }

    public void onClcFilterButton(View view){
        transaction = manager.beginTransaction();

        if(flag == true){
            transaction.add(R.id.filterFragmentFood, filterFragment); flag = false;}
        else {transaction.remove(filterFragment); flag = true;}

        transaction.commit();
    }



    public JSONArray getFood() throws InterruptedException,
            ExecutionException{

        //RecycleFoodCatalogActivity.GetFood get = new RecycleFoodCatalogActivity.GetFood();
        //get.execute("http://caloriesdiary.ru/food/get_food",String.valueOf(offset));


       // return get.get();
        return null;
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
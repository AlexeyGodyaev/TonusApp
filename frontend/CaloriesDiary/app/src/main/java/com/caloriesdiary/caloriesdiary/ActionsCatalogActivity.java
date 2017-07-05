package com.caloriesdiary.caloriesdiary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class ActionsCatalogActivity extends FragmentActivity {

    EditText srch;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actions_catalog_layout);

        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);

        srch = (EditText) findViewById(R.id.srchAction);
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

        listView = (ListView) findViewById(R.id.actionsList);

        ActionsAdapter adapter = new ActionsAdapter(this, initData());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsn = new JSONObject();
                final TextView txtName = (TextView) view.findViewById(R.id.productName);
                final TextView txtCalories = (TextView) view.findViewById(R.id.productCalories);
                String kcalstr = txtCalories.getText().toString().substring(0, txtCalories.getText().toString().indexOf('.'));
                final double kcal = Double.parseDouble(kcalstr);
                final TextView kcaltextview = new TextView(ActionsCatalogActivity.this);
                kcaltextview.setText(kcalstr + " kcal");
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActionsCatalogActivity.this);
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

                final EditText input = new EditText(ActionsCatalogActivity.this);
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
                                input.setText("0");
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
        });
    }

    public  String getAction() throws InterruptedException, ExecutionException {
        GetActions get = new GetActions();
        get.execute("http://192.168.1.205/activities/get_activities");

        return get.get().toString();
    }

    private List<ActionItem> initData() {
        List<ActionItem> list = new ArrayList<ActionItem>();
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

package com.caloriesdiary.caloriesdiary.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import com.caloriesdiary.caloriesdiary.Items.ActionItem;
import com.caloriesdiary.caloriesdiary.Adapters.ActionsAdapter;
import com.caloriesdiary.caloriesdiary.Posts.GetActions;
import com.caloriesdiary.caloriesdiary.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActionsCatalogActivity extends FragmentActivity {

    private EditText srch;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actions_catalog_layout);

        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);

        srch = findViewById(R.id.srchAction);
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

        listView =  findViewById(R.id.actionsList);

        ActionsAdapter adapter = new ActionsAdapter(this, initData());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final TextView txtName =  view.findViewById(R.id.productName);
                final TextView txtCalories = view.findViewById(R.id.productCalories);
                final double kcal =  Double.parseDouble(txtCalories.getText().toString().substring(0, txtCalories.getText().toString().indexOf('.')));
                final TextView kcaltextview = new TextView(ActionsCatalogActivity.this);
                kcaltextview.setText(kcal + " ккал");

                final AlertDialog.Builder builder = new AlertDialog.Builder(ActionsCatalogActivity.this);
                builder.setTitle(txtName.getText().toString())
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

                                        if ( lp.getChildCount() > 0)
                                            lp.removeAllViews();
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
                                newkcal = Math.round(kcal * time / 60);
                                //Toast.makeText(getApplicationContext(),String.valueOf(newkcal),Toast.LENGTH_LONG).show();
                                kcaltextview.setText(newkcal + " ккал");
                            } else {
                                Toast.makeText(getApplicationContext(), "Не пизди", Toast.LENGTH_LONG).show();
                                input.setText("0");
                            }

                        } else {
                            kcaltextview.setText(newkcal + " ккал");

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

    private String getAction() throws InterruptedException, ExecutionException {
        GetActions get = new GetActions();
        get.execute("http://caloriesdiary.ru/activities/get_activities");

        return get.get();
    }

    private List<ActionItem> initData() {
        try {

            List<ActionItem> list = new ArrayList<>();
            String resp;
            String actionName;
            Float calories;
            Integer id;

            resp = getAction();

            if (resp != null)
                while (resp.length() > 10) {

                    id = Integer.valueOf(resp.substring(0, resp.indexOf(',')));

                    resp = resp.substring(resp.indexOf(',') + 1);

                    actionName = resp.substring(0, resp.indexOf(','));
                    resp = resp.substring(resp.indexOf(',') + 1);

                    calories = Float.valueOf(resp.substring(0, resp.indexOf(';')));

                    resp = resp.substring(resp.indexOf(';') + 1);

                    if (calories != 0)
                        list.add(new ActionItem(actionName, calories, id));
                }

            return list;

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

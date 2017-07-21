package com.caloriesdiary.caloriesdiary;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class StatActivity extends AppCompatActivity {
    TextView setGraphs, massGraphTxt, eatedCaloriesGraphTxt, bernCaloriesGraphTxt, rLegGraphTxt, lLegGraphTxt,
            calvesGraphTxt, buttGraphTxt, rHandGraphTxt, lHandGraphTxt, chestGraphTxt, shouldersGraphTxt, waistGraphTxt, viewParams;
    LinearLayout mainLayout;
    GraphView massGraph, eatedCaloriesGraph, bernCaloriesGraph, rLegGraph, lLegGraph, calvesGraph, buttGraph, rHandGraph,
            lHandGraph, chestGraph, shouldersGraph, waistGraph;
    JSONArray graphArr=null;
    JSONObject graphDraw=null;
    DataPoint massData [], eatedData[], bernData [], rLegData [], lLegData [], rHandData [],
    lHandData [], waistData [], chestData [], buttData [], shouldersData [], calvesData [];
    String graphHor [];
    String s="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_layout);

        viewParams = (TextView) findViewById(R.id.graph_params_value);
        mainLayout = (LinearLayout) findViewById(R.id.hui);
        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);
        int a;
        setGraphs = (TextView) findViewById(R.id.graph_params);
        setTitle("Статистика");

        try {
            File f = new File(getCacheDir(), "Graph_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();
                graphDraw = new JSONObject(text);
            }
        }  catch (Exception e){

        }

        setGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CheckBox massCheck, eatedCheck, bernCheck, buttCheck, shouldersCheck,  calvesCheck, chestCheck,
                        waistCheck, handsCheck, legsCheck;
                massCheck = new CheckBox(StatActivity.this);
                massCheck.setText("Масса");
                eatedCheck = new CheckBox(StatActivity.this);
                eatedCheck.setText("Потребленные калории");
                bernCheck = new CheckBox(StatActivity.this);
                bernCheck.setText("Затраченные калории");
                buttCheck = new CheckBox(StatActivity.this);
                buttCheck.setText("Ягодицы");
                shouldersCheck = new CheckBox(StatActivity.this);
                shouldersCheck.setText("Плечи");
                calvesCheck = new CheckBox(StatActivity.this);
                calvesCheck.setText("Икры");
                chestCheck = new CheckBox(StatActivity.this);
                chestCheck.setText("Грудь");
                waistCheck = new CheckBox(StatActivity.this);
                waistCheck.setText("Талия");
                handsCheck = new CheckBox(StatActivity.this);
                handsCheck.setText("Руки");
                legsCheck = new CheckBox(StatActivity.this);
                legsCheck.setText("Бедра");

                lp.addView(massCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(eatedCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(bernCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(shouldersCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(handsCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(chestCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(waistCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(buttCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(legsCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lp.addView(calvesCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                try {
                    File f = new File(getCacheDir(), "Graph_params.txt");
                    if (f.exists()) {
                        FileInputStream in = new FileInputStream(f);
                        ObjectInputStream inObject = new ObjectInputStream(in);
                        String text = inObject.readObject().toString();
                        inObject.close();
                        JSONObject jsn = new JSONObject(text);

                        if (jsn.getString("mass").equals("true"))
                            massCheck.setChecked(true);
                        if (jsn.getString("eated").equals("true"))
                            eatedCheck.setChecked(true);
                        if (jsn.getString("bern").equals("true"))
                            bernCheck.setChecked(true);
                        if (jsn.getString("shoulders").equals("true"))
                            shouldersCheck.setChecked(true);
                        if (jsn.getString("hands").equals("true"))
                            handsCheck.setChecked(true);
                        if (jsn.getString("chest").equals("true"))
                            chestCheck.setChecked(true);
                        if (jsn.getString("waist").equals("true"))
                            waistCheck.setChecked(true);
                        if (jsn.getString("butt").equals("true"))
                            buttCheck.setChecked(true);
                        if (jsn.getString("legs").equals("true"))
                            legsCheck.setChecked(true);
                        if (jsn.getString("calves").equals("true"))
                            calvesCheck.setChecked(true);

                        f.createNewFile();
                    }
                }  catch (Exception e){

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(StatActivity.this);
                builder.setTitle("Выберите отображаемые графики")
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
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {
                                    JSONObject jsn;
                                    File f = new File(getCacheDir(), "Graph_params.txt");

                                    jsn = new JSONObject();

                                    jsn.put("mass", massCheck.isChecked());
                                    jsn.put("shoulders", shouldersCheck.isChecked());
                                    jsn.put("calves", calvesCheck.isChecked());
                                    jsn.put("chest", chestCheck.isChecked());
                                    jsn.put("waist", waistCheck.isChecked());
                                    jsn.put("butt", buttCheck.isChecked());
                                    jsn.put("bern", bernCheck.isChecked());
                                    jsn.put("eated", eatedCheck.isChecked());
                                    jsn.put("legs", legsCheck.isChecked());
                                    jsn.put("hands", handsCheck.isChecked());


                                    FileOutputStream out = new FileOutputStream(f);
                                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                                    outObject.writeObject(jsn.toString());
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

                                recreate();
                            }
                        });

                builder.setView(lp);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        try {
            JSONObject jsn;
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                graphArr = jsn.getJSONArray("today_params");
                jsn.remove("today_params");
            }


        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        massGraph = (GraphView) findViewById(R.id.mass_graph_container);
        eatedCaloriesGraph = (GraphView) findViewById(R.id.eated_calories_graph_container);
        bernCaloriesGraph = (GraphView) findViewById(R.id.bern_calories_graph_container);
        shouldersGraph = (GraphView) findViewById(R.id.shoulders_graph_container);
        rHandGraph = (GraphView) findViewById(R.id.right_hand_graph_container);
        lHandGraph = (GraphView) findViewById(R.id.left_hand_graph_container);
        lLegGraph = (GraphView) findViewById(R.id.left_leg_graph_container);
        rLegGraph = (GraphView) findViewById(R.id.right_leg_graph_container);
        calvesGraph = (GraphView) findViewById(R.id.calves_graph_container);
        buttGraph = (GraphView) findViewById(R.id.butt_graph_container);
        chestGraph = (GraphView) findViewById(R.id.chest_graph_container);
        waistGraph = (GraphView) findViewById(R.id.waist_graph_container);

        massGraphTxt = (TextView) findViewById(R.id.mass_text);
        eatedCaloriesGraphTxt = (TextView) findViewById(R.id.eated_text);
        bernCaloriesGraphTxt = (TextView) findViewById(R.id.bern_text);
        shouldersGraphTxt = (TextView) findViewById(R.id.shoulders_text);
        rHandGraphTxt = (TextView) findViewById(R.id.rh_text);
        lHandGraphTxt = (TextView) findViewById(R.id.lh_text);
        lLegGraphTxt = (TextView) findViewById(R.id.ll_text);
        rLegGraphTxt = (TextView) findViewById(R.id.rl_text);
        calvesGraphTxt = (TextView) findViewById(R.id.calves_text);
        buttGraphTxt = (TextView) findViewById(R.id.butt_text);
        chestGraphTxt = (TextView) findViewById(R.id.chest_text);
        waistGraphTxt = (TextView) findViewById(R.id.waist_text);

        try {
            if (graphArr!=null&&graphArr.length()>1) {

                massData = new DataPoint[graphArr.length()];
                eatedData = new DataPoint[graphArr.length()];
                rLegData = new DataPoint[graphArr.length()];
                lLegData = new DataPoint[graphArr.length()];
                rHandData = new DataPoint[graphArr.length()];
                lHandData = new DataPoint[graphArr.length()];
                calvesData = new DataPoint[graphArr.length()];
                buttData = new DataPoint[graphArr.length()];
                waistData = new DataPoint[graphArr.length()];
                shouldersData = new DataPoint[graphArr.length()];
                chestData = new DataPoint[graphArr.length()];
                bernData = new DataPoint[graphArr.length()];
                graphHor = new String[graphArr.length()];

                if (graphArr.length()>7)
                    a = graphArr.length()-8; else a = 0;

                for (int i=a; i < graphArr.length(); i++) {
                    if (graphArr.getJSONObject(i).getString("mass").equals(""))
                        if(i==0) massData[i] = new DataPoint(i, 0); else
                             massData[i] = new DataPoint(i, massData[i-1].getY());
                    else
                        massData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("mass")));

                    if (graphArr.getJSONObject(i).getString("eatedCalories").equals(""))
                        if(i==0) eatedData[i] = new DataPoint(i, 0); else
                            eatedData[i] = new DataPoint(i, eatedData[i-1].getY());
                    else
                         eatedData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("eatedCalories")));

                    if (graphArr.getJSONObject(i).getString("bernCalories").equals(""))
                        if(i==0) bernData[i] = new DataPoint(i, 0); else
                            bernData[i] = new DataPoint(i, bernData[i-1].getY());
                    else
                        bernData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("bernCalories")));

                    if (graphArr.getJSONObject(i).getString("rLeg").equals(""))
                        if(i==0) rLegData[i] = new DataPoint(i, 0); else
                            rLegData[i] = new DataPoint(i, rLegData[i-1].getY());
                    else
                        rLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rLeg")));

                    if (graphArr.getJSONObject(i).getString("lLeg").equals(""))
                        if(i==0) lLegData[i] = new DataPoint(i, 0); else
                            lLegData[i] = new DataPoint(i, lLegData[i-1].getY());
                    else
                    lLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lLeg")));

                    if (graphArr.getJSONObject(i).getString("rHand").equals(""))
                        if(i==0) rHandData[i] = new DataPoint(i, 0); else
                            rHandData[i] = new DataPoint(i, rHandData[i-1].getY());
                    else
                    rHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rHand")));

                    if (graphArr.getJSONObject(i).getString("lHand").equals(""))
                        if(i==0) lHandData[i] = new DataPoint(i, 0); else
                            lHandData[i] = new DataPoint(i, lHandData[i-1].getY());
                    else
                    lHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lHand")));

                    if (graphArr.getJSONObject(i).getString("waist").equals(""))
                        if(i==0) waistData[i] = new DataPoint(i, 0); else
                            waistData[i] = new DataPoint(i, waistData[i-1].getY());
                    else
                    waistData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("waist")));

                    if (graphArr.getJSONObject(i).getString("chest").equals(""))
                        if(i==0) chestData[i] = new DataPoint(i, 0); else
                            chestData[i] = new DataPoint(i, chestData[i-1].getY());
                    else
                    chestData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("chest")));

                    if (graphArr.getJSONObject(i).getString("shoulders").equals(""))
                        if(i==0) shouldersData[i] = new DataPoint(i, 0); else
                            shouldersData[i] = new DataPoint(i, shouldersData[i-1].getY());
                    else
                    shouldersData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("shoulders")));

                    if (graphArr.getJSONObject(i).getString("butt").equals(""))
                        if(i==0) buttData[i] = new DataPoint(i, 0); else
                            buttData[i] = new DataPoint(i,  buttData[i-1].getY());
                    else
                    buttData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("butt")));

                    if (graphArr.getJSONObject(i).getString("calves").equals(""))
                        if(i==0) calvesData[i] = new DataPoint(i, 0); else
                            calvesData[i] = new DataPoint(i, calvesData[i-1].getY());
                    else
                    calvesData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("calves")));
                    graphHor[i] = graphArr.getJSONObject(i).getString("date");
                }

                if(graphDraw!=null&&graphDraw.getString("mass").equals("false")) {
                    mainLayout.removeView(massGraph);
                    mainLayout.removeView(massGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> massSeries = new LineGraphSeries<>(massData);
                    StaticLabelsFormatter massLabelsFormatter = new StaticLabelsFormatter(massGraph);
                    massLabelsFormatter.setHorizontalLabels(graphHor);
                    massGraph.getGridLabelRenderer().setLabelFormatter(massLabelsFormatter);
                    massGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    massGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    massGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    massGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    massSeries.setColor(Color.WHITE);
                    massGraph.addSeries(massSeries);
                    s+="Масса, ";
                }

                if(graphDraw!=null&&graphDraw.getString("eated").equals("false")) {
                    mainLayout.removeView(eatedCaloriesGraph);
                    mainLayout.removeView(eatedCaloriesGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> eatedSeries = new LineGraphSeries<>(eatedData);
                    StaticLabelsFormatter eatedLabelsFormatter = new StaticLabelsFormatter(eatedCaloriesGraph);
                    eatedLabelsFormatter.setHorizontalLabels(graphHor);
                    eatedCaloriesGraph.getGridLabelRenderer().setLabelFormatter(eatedLabelsFormatter);
                    eatedCaloriesGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    eatedCaloriesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    eatedCaloriesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    eatedCaloriesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    eatedSeries.setColor(Color.WHITE);
                    eatedCaloriesGraph.addSeries(eatedSeries);
                    s+="Потребляемые калории, ";
                }

                if(graphDraw!=null&&graphDraw.getString("bern").equals("false")) {
                    mainLayout.removeView(bernCaloriesGraph);
                    mainLayout.removeView(bernCaloriesGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> bernSeries = new LineGraphSeries<>(bernData);
                    StaticLabelsFormatter bernLabelsFormatter = new StaticLabelsFormatter(bernCaloriesGraph);
                    bernLabelsFormatter.setHorizontalLabels(graphHor);
                    bernCaloriesGraph.getGridLabelRenderer().setLabelFormatter(bernLabelsFormatter);
                    bernCaloriesGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    bernCaloriesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    bernCaloriesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    bernCaloriesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    bernSeries.setColor(Color.WHITE);
                    bernCaloriesGraph.addSeries(bernSeries);
                    s+="Затраченные калории, ";
                }

                if(graphDraw!=null&&graphDraw.getString("shoulders").equals("false")) {
                    mainLayout.removeView(shouldersGraph);
                    mainLayout.removeView(shouldersGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> shouldersSeries = new LineGraphSeries<>(shouldersData);
                    StaticLabelsFormatter shouldersLabelsFormatter = new StaticLabelsFormatter(shouldersGraph);
                    shouldersLabelsFormatter.setHorizontalLabels(graphHor);
                    shouldersGraph.getGridLabelRenderer().setLabelFormatter(shouldersLabelsFormatter);
                    shouldersGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    shouldersGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    shouldersGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    shouldersGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    shouldersSeries.setColor(Color.WHITE);
                    shouldersGraph.addSeries(shouldersSeries);
                    s+="Плечи, ";
                }

                if(graphDraw!=null&&graphDraw.getString("hands").equals("false")){
                    mainLayout.removeView(rHandGraph);
                    mainLayout.removeView(lHandGraph);
                    mainLayout.removeView(rHandGraphTxt);
                    mainLayout.removeView(lHandGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> rHandSeries = new LineGraphSeries<>(rHandData);
                    StaticLabelsFormatter rHandLabelsFormatter = new StaticLabelsFormatter(rHandGraph);
                    rHandLabelsFormatter.setHorizontalLabels(graphHor);
                    rHandGraph.getGridLabelRenderer().setLabelFormatter(rHandLabelsFormatter);
                    rHandGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    rHandGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    rHandGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    rHandGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    rHandSeries.setColor(Color.WHITE);
                    rHandGraph.addSeries(rHandSeries);

                    LineGraphSeries<DataPoint> lHandSeries = new LineGraphSeries<>(lHandData);
                    StaticLabelsFormatter lHandLabelsFormatter = new StaticLabelsFormatter(lHandGraph);
                    lHandLabelsFormatter.setHorizontalLabels(graphHor);
                    lHandGraph.getGridLabelRenderer().setLabelFormatter(lHandLabelsFormatter);
                    lHandGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    lHandGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    lHandGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    lHandGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    lHandSeries.setColor(Color.WHITE);
                    lHandGraph.addSeries(lHandSeries);
                    s+="Руки, ";
                }

                if(graphDraw!=null&&graphDraw.getString("chest").equals("false")) {
                    mainLayout.removeView(chestGraph);
                    mainLayout.removeView(chestGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> chestSeries = new LineGraphSeries<>(chestData);
                    StaticLabelsFormatter chestLabelsFormatter = new StaticLabelsFormatter(chestGraph);
                    chestLabelsFormatter.setHorizontalLabels(graphHor);
                    chestGraph.getGridLabelRenderer().setLabelFormatter(chestLabelsFormatter);
                    chestGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    chestGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    chestGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    chestGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    chestSeries.setColor(Color.WHITE);
                    chestGraph.addSeries(chestSeries);
                    s+="Грудь, ";
                }

                if(graphDraw!=null&&graphDraw.getString("waist").equals("false")) {
                    mainLayout.removeView(waistGraph);
                    mainLayout.removeView(waistGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> waistSeries = new LineGraphSeries<>(waistData);
                    StaticLabelsFormatter waistLabelsFormatter = new StaticLabelsFormatter(waistGraph);
                    waistLabelsFormatter.setHorizontalLabels(graphHor);
                    waistGraph.getGridLabelRenderer().setLabelFormatter(waistLabelsFormatter);
                    waistGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    waistGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    waistGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    waistGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    waistSeries.setColor(Color.WHITE);
                    waistGraph.addSeries(waistSeries);
                    s+="Талия, ";
                }

                if(graphDraw!=null&&graphDraw.getString("butt").equals("false")) {
                    mainLayout.removeView(buttGraph);
                    mainLayout.removeView(buttGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> buttSeries = new LineGraphSeries<>(buttData);
                    StaticLabelsFormatter buttLabelsFormatter = new StaticLabelsFormatter(buttGraph);
                    buttLabelsFormatter.setHorizontalLabels(graphHor);
                    buttGraph.getGridLabelRenderer().setLabelFormatter(buttLabelsFormatter);
                    buttGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    buttGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    buttGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    buttGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    buttSeries.setColor(Color.WHITE);
                    buttGraph.addSeries(buttSeries);
                    s+="Ягодицы, ";
                }

                if(graphDraw!=null&&graphDraw.getString("legs").equals("false")){
                    mainLayout.removeView(lLegGraph);
                    mainLayout.removeView(rLegGraph);
                    mainLayout.removeView(lLegGraphTxt);
                    mainLayout.removeView(rLegGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> rLegSeries = new LineGraphSeries<>(rLegData);
                    StaticLabelsFormatter rLegLabelsFormatter = new StaticLabelsFormatter(rLegGraph);
                    rLegLabelsFormatter.setHorizontalLabels(graphHor);
                    rLegGraph.getGridLabelRenderer().setLabelFormatter(rLegLabelsFormatter);
                    rLegGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    rLegGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    rLegGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    rLegGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    rLegSeries.setColor(Color.WHITE);
                    rLegGraph.addSeries(rLegSeries);

                    LineGraphSeries<DataPoint> lLegSeries = new LineGraphSeries<>(lLegData);
                    StaticLabelsFormatter lLegLabelsFormatter = new StaticLabelsFormatter(lLegGraph);
                    lLegLabelsFormatter.setHorizontalLabels(graphHor);
                    lLegGraph.getGridLabelRenderer().setLabelFormatter(lLegLabelsFormatter);
                    lLegGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    lLegGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    lLegGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    lLegGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    lLegSeries.setColor(Color.WHITE);
                    lLegGraph.addSeries(lLegSeries);
                    s+="Бедра, ";
                }


                if(graphDraw!=null&&graphDraw.getString("calves").equals("false")) {
                    mainLayout.removeView(calvesGraph);
                    mainLayout.removeView(calvesGraphTxt);
                } else {
                    LineGraphSeries<DataPoint> calvesSeries = new LineGraphSeries<>(calvesData);
                    StaticLabelsFormatter calvesLabelsFormatter = new StaticLabelsFormatter(calvesGraph);
                    calvesLabelsFormatter.setHorizontalLabels(graphHor);
                    calvesGraph.getGridLabelRenderer().setLabelFormatter(calvesLabelsFormatter);
                    calvesGraph.setBackgroundColor(Color.parseColor("#A5CDCD"));
                    calvesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    calvesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    calvesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    calvesSeries.setColor(Color.WHITE);
                    calvesGraph.addSeries(calvesSeries);
                    s+= "Икры, ";
                }

                s = s.substring(0, s.length()-2)+".";
                viewParams.setText(s);
            }
            else Toast.makeText(this, "Статистика доступна после двух заполненных дней", Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
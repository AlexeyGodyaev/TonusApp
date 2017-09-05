package com.caloriesdiary.caloriesdiary.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.R;
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

    private GraphView massGraph, eatedCaloriesGraph, bernCaloriesGraph, rLegGraph, lLegGraph, calvesGraph, buttGraph, rHandGraph,
            lHandGraph, chestGraph, shouldersGraph, waistGraph;
    private TextView setGraphs, massGraphTxt, eatedCaloriesGraphTxt, bernCaloriesGraphTxt, rLegGraphTxt, lLegGraphTxt,
            calvesGraphTxt, buttGraphTxt, rHandGraphTxt, lHandGraphTxt, chestGraphTxt, shouldersGraphTxt, waistGraphTxt, viewParams;
    private LinearLayout mainLayout;
    private JSONArray graphArr=null, tableDataArray = null;
    private JSONObject graphDraw=null;
    private String s="";
    private String graphHor [];
    private TableLayout table;

    private DataPoint massData [], eatedData[], bernData [], rLegData [], lLegData [], rHandData [],
            lHandData [], waistData [], chestData [], buttData [], shouldersData [], calvesData [];

    private TableRow rowDayLabels, rowButt, rowLHand, rowRHand, rowRLeg, rowLLeg, rowCalves,
            rowShoulders, rowChest, rowWaist, rowMass, rowBurnCalories, rowEatedCalories;

    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.stat_layout);
            setTitle("Статистика");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            TabWidget tabs = (TabWidget) findViewById(android.R.id.tabs);
            tabs.setElevation(getSupportActionBar().getElevation());
            getSupportActionBar().setElevation(0);

            initTabs();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);

        table = (TableLayout) findViewById(R.id.table_layout);
        viewParams = (TextView) findViewById(R.id.graph_params_value);
        mainLayout = (LinearLayout) findViewById(R.id.graph_layout);
        setGraphs = (TextView) findViewById(R.id.graph_params);

        try {
            File f = new File(getCacheDir(), "Graph_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();
                graphDraw = new JSONObject(text);
            } else {
                graphDraw = new JSONObject();
                graphDraw.put("mass", "true");
                graphDraw.put("shoulders", "true");
                graphDraw.put("calves", "true");
                graphDraw.put("chest", "true");
                graphDraw.put("waist", "true");
                graphDraw.put("butt", "true");
                graphDraw.put("bern", "true");
                graphDraw.put("eated", "true");
                graphDraw.put("legs", "true");
                graphDraw.put("hands", "true");

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        setGraphListener();

        try {
            JSONObject jsn;
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();

                jsn = new JSONObject(text);
                graphArr = jsn.getJSONArray("days");
                jsn.remove("days");

                if (graphArr.length() > 7) {
                    JSONArray buf = new JSONArray();
                    for (int i = graphArr.length() - 7; i < graphArr.length(); i++) {
                        buf.put(graphArr.getJSONObject(i));
                    }
                    graphArr = new JSONArray(buf.toString());
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }


        initGraphs();

        try {
            if (graphArr != null && graphArr.length() > 2) {

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

                setGraphData();

                setDrawingGraphs();
            } else
                Toast.makeText(getApplicationContext(), "Графики доступны после трех заполненных дней", Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }


        table.setStretchAllColumns(true);

        rowDayLabels = new TableRow(getApplicationContext());
        rowMass = new TableRow(getApplicationContext());
        rowBurnCalories = new TableRow(getApplicationContext());
        rowEatedCalories = new TableRow(getApplicationContext());
        rowShoulders = new TableRow(getApplicationContext());
        rowLHand = new TableRow(getApplicationContext());
        rowRHand = new TableRow(getApplicationContext());
        rowCalves = new TableRow(getApplicationContext());
        rowLLeg = new TableRow(getApplicationContext());
        rowRLeg = new TableRow(getApplicationContext());
        rowChest = new TableRow(getApplicationContext());
        rowWaist = new TableRow(getApplicationContext());
        rowButt = new TableRow(getApplicationContext());

        initRows();

        setRowData();

    }


    private void setGraphListener(){
        setGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CheckBox massCheck, eatedCheck, bernCheck, buttCheck, shouldersCheck,  calvesCheck, chestCheck,
                        waistCheck, handsCheck, legsCheck;
                final LinearLayout lp = new LinearLayout(getApplicationContext());
                lp.setOrientation(LinearLayout.VERTICAL);

                try{
                    massCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    massCheck.setText("Масса");
                    massCheck.setEnabled(false);
                    eatedCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    eatedCheck.setText("Потребленные калории");
                    bernCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    bernCheck.setText("Затраченные калории");
                    buttCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    buttCheck.setText("Ягодицы");
                    shouldersCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    shouldersCheck.setText("Плечи");
                    calvesCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    calvesCheck.setText("Икры");
                    chestCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    chestCheck.setText("Грудь");
                    waistCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    waistCheck.setText("Талия");
                    handsCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
                    handsCheck.setText("Руки");
                    legsCheck = new CheckBox(new ContextThemeWrapper(StatActivity.this, R.style.MyCheckBox));
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
                    massCheck.setChecked(true);
                    eatedCheck.setChecked(true);
                    bernCheck.setChecked(true);
                    shouldersCheck.setChecked(true);
                    handsCheck.setChecked(true);
                    chestCheck.setChecked(true);
                    waistCheck.setChecked(true);
                    buttCheck.setChecked(true);
                    legsCheck.setChecked(true);
                    calvesCheck.setChecked(true);

                    File f = new File(getCacheDir(), "Graph_params.txt");
                    if (f.exists()) {
                        FileInputStream in = new FileInputStream(f);
                        ObjectInputStream inObject = new ObjectInputStream(in);
                        String text = inObject.readObject().toString();
                        inObject.close();
                        JSONObject jsn = new JSONObject(text);

                        if (jsn.getString("mass").equals("false"))
                            massCheck.setChecked(false);
                        if (jsn.getString("eated").equals("false"))
                            eatedCheck.setChecked(false);
                        if (jsn.getString("bern").equals("false"))
                            bernCheck.setChecked(false);
                        if (jsn.getString("shoulders").equals("false"))
                            shouldersCheck.setChecked(false);
                        if (jsn.getString("hands").equals("false"))
                            handsCheck.setChecked(false);
                        if (jsn.getString("chest").equals("false"))
                            chestCheck.setChecked(false);
                        if (jsn.getString("waist").equals("false"))
                            waistCheck.setChecked(false);
                        if (jsn.getString("butt").equals("false"))
                            buttCheck.setChecked(false);
                        if (jsn.getString("legs").equals("false"))
                            legsCheck.setChecked(false);
                        if (jsn.getString("calves").equals("false"))
                            calvesCheck.setChecked(false);

                        f.createNewFile();
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

                                       recreate();
                                    }
                                    catch (Exception iEx){
                                        Toast.makeText(getApplicationContext(), iEx.toString() , Toast.LENGTH_LONG).show();
                                    }

                                    if (lp.getChildCount() > 0)
                                        lp.removeAllViews();
                                    ((ViewManager) lp.getParent()).removeView(lp);


                                }
                            });

                    builder.setView(lp);
                    AlertDialog alert = builder.create();
                    alert.show();

                }  catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString() , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initGraphs(){
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

        massGraph = (GraphView) findViewById(R.id.mass_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("mass").equals("false")) {
                mainLayout.removeView(massGraph);
                mainLayout.removeView(massGraphTxt);
            } else {
                massGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                massGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                massGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                massGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Масса, ";
            }
        } catch (Exception e){

        }

        eatedCaloriesGraph = (GraphView) findViewById(R.id.eated_calories_graph_container);
        try {
            if (graphDraw != null && graphDraw.getString("eated").equals("false")) {
                mainLayout.removeView(eatedCaloriesGraph);
                mainLayout.removeView(eatedCaloriesGraphTxt);
            } else {
                eatedCaloriesGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                eatedCaloriesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                eatedCaloriesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                eatedCaloriesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Потребляемые калории, ";
            }
        }catch (Exception e){

        }

        bernCaloriesGraph = (GraphView) findViewById(R.id.bern_calories_graph_container);
        try {
            if (graphDraw != null && graphDraw.getString("bern").equals("false")) {
                mainLayout.removeView(bernCaloriesGraph);
                mainLayout.removeView(bernCaloriesGraphTxt);
            } else {
                bernCaloriesGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                bernCaloriesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                bernCaloriesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                bernCaloriesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Затраченные калории, ";
            }
        }catch (Exception e){

        }

        shouldersGraph = (GraphView) findViewById(R.id.shoulders_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("shoulders").equals("false")) {
                mainLayout.removeView(shouldersGraph);
                mainLayout.removeView(shouldersGraphTxt);
            } else {
                shouldersGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                shouldersGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                shouldersGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                shouldersGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Плечи, ";
            }
        } catch (Exception e){

        }

        rHandGraph = (GraphView) findViewById(R.id.right_hand_graph_container);
        lHandGraph = (GraphView) findViewById(R.id.left_hand_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("hands").equals("false")){
                mainLayout.removeView(rHandGraph);
                mainLayout.removeView(lHandGraph);
                mainLayout.removeView(rHandGraphTxt);
                mainLayout.removeView(lHandGraphTxt);
            } else {
                rHandGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                rHandGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                rHandGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                rHandGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

                lHandGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                lHandGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                lHandGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                lHandGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Руки, ";
            }
        } catch (Exception e) {

        }

        lLegGraph = (GraphView) findViewById(R.id.left_leg_graph_container);
        rLegGraph = (GraphView) findViewById(R.id.right_leg_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("legs").equals("false")){
                mainLayout.removeView(lLegGraph);
                mainLayout.removeView(rLegGraph);
                mainLayout.removeView(lLegGraphTxt);
                mainLayout.removeView(rLegGraphTxt);
            } else {
                lLegGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                lLegGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                lLegGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                lLegGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

                rLegGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                rLegGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                rLegGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                rLegGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Бедра, ";
            }
        } catch (Exception e) {

        }

        calvesGraph = (GraphView) findViewById(R.id.calves_graph_container);
        try {if(graphDraw!=null&&graphDraw.getString("calves").equals("false")) {
            mainLayout.removeView(calvesGraph);
            mainLayout.removeView(calvesGraphTxt);
        } else {
            calvesGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
            calvesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
            calvesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
            calvesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
            s+= "Икры, ";
        }
        } catch (Exception e){

        }

        buttGraph = (GraphView) findViewById(R.id.butt_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("butt").equals("false")) {
                mainLayout.removeView(buttGraph);
                mainLayout.removeView(buttGraphTxt);
            } else {
                buttGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                buttGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                buttGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                buttGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Ягодицы, ";
            }
        } catch (Exception e){

        }

        chestGraph = (GraphView) findViewById(R.id.chest_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("chest").equals("false")) {
                mainLayout.removeView(chestGraph);
                mainLayout.removeView(chestGraphTxt);
            } else {
                chestGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                chestGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                chestGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                chestGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Грудь, ";
            }
        } catch (Exception e){

        }

        waistGraph = (GraphView) findViewById(R.id.waist_graph_container);
        try {
            if(graphDraw!=null&&graphDraw.getString("waist").equals("false")) {
                mainLayout.removeView(waistGraph);
                mainLayout.removeView(waistGraphTxt);
            } else {
                waistGraph.setBackgroundColor(Color.parseColor("#41A3E4"));
                waistGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                waistGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                waistGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                s+="Талия, ";
            }
        } catch (Exception e){

        }

        s = s.substring(0, s.length()-2)+".";
        viewParams.setText(s);

    }

    private void setGraphData(){
        try {
            for (int i = 0; i < graphArr.length(); i++) {
                if (graphArr.getJSONObject(i).getString("mass").equals(""))
                    if (i == 0) massData[i] = new DataPoint(i, 0);
                    else
                        massData[i] = new DataPoint(i, massData[i - 1].getY());
                else
                    massData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("mass")));

                if (graphArr.getJSONObject(i).getString("food_sum").equals(""))
                    if (i == 0) eatedData[i] = new DataPoint(i, 0);
                    else
                        eatedData[i] = new DataPoint(i, eatedData[i - 1].getY());
                else
                    eatedData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("food_sum")));

                if (graphArr.getJSONObject(i).getString("active_sum").equals(""))
                    if (i == 0) bernData[i] = new DataPoint(i, 0);
                    else
                        bernData[i] = new DataPoint(i, bernData[i - 1].getY());
                else
                    bernData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("active_sum")));

                if (graphArr.getJSONObject(i).getString("rLeg").equals(""))
                    if (i == 0) rLegData[i] = new DataPoint(i, 0);
                    else
                        rLegData[i] = new DataPoint(i, rLegData[i - 1].getY());
                else
                    rLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rLeg")));

                if (graphArr.getJSONObject(i).getString("lLeg").equals(""))
                    if (i == 0) lLegData[i] = new DataPoint(i, 0);
                    else
                        lLegData[i] = new DataPoint(i, lLegData[i - 1].getY());
                else
                    lLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lLeg")));

                if (graphArr.getJSONObject(i).getString("rHand").equals(""))
                    if (i == 0) rHandData[i] = new DataPoint(i, 0);
                    else
                        rHandData[i] = new DataPoint(i, rHandData[i - 1].getY());
                else
                    rHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rHand")));

                if (graphArr.getJSONObject(i).getString("lHand").equals(""))
                    if (i == 0) lHandData[i] = new DataPoint(i, 0);
                    else
                        lHandData[i] = new DataPoint(i, lHandData[i - 1].getY());
                else
                    lHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lHand")));

                if (graphArr.getJSONObject(i).getString("waist").equals(""))
                    if (i == 0) waistData[i] = new DataPoint(i, 0);
                    else
                        waistData[i] = new DataPoint(i, waistData[i - 1].getY());
                else
                    waistData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("waist")));

                if (graphArr.getJSONObject(i).getString("chest").equals(""))
                    if (i == 0) chestData[i] = new DataPoint(i, 0);
                    else
                        chestData[i] = new DataPoint(i, chestData[i - 1].getY());
                else
                    chestData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("chest")));

                if (graphArr.getJSONObject(i).getString("shoulders").equals(""))
                    if (i == 0) shouldersData[i] = new DataPoint(i, 0);
                    else
                        shouldersData[i] = new DataPoint(i, shouldersData[i - 1].getY());
                else
                    shouldersData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("shoulders")));

                if (graphArr.getJSONObject(i).getString("butt").equals(""))
                    if (i == 0) buttData[i] = new DataPoint(i, 0);
                    else
                        buttData[i] = new DataPoint(i, buttData[i - 1].getY());
                else
                    buttData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("butt")));

                if (graphArr.getJSONObject(i).getString("calves").equals(""))
                    if (i == 0) calvesData[i] = new DataPoint(i, 0);
                    else
                        calvesData[i] = new DataPoint(i, calvesData[i - 1].getY());
                else
                    calvesData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("calves")));
                graphHor[i] = graphArr.getJSONObject(i).getString("date")
                        .substring(8, graphArr.getJSONObject(i).getString("date").length());
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDrawingGraphs(){
        try {
            if (graphDraw != null && graphDraw.getString("mass").equals("true")) {
                LineGraphSeries<DataPoint> massSeries = new LineGraphSeries<>(massData);
                StaticLabelsFormatter massLabelsFormatter = new StaticLabelsFormatter(massGraph);
                massLabelsFormatter.setHorizontalLabels(graphHor);
                massGraph.getGridLabelRenderer().setLabelFormatter(massLabelsFormatter);
                massSeries.setColor(Color.WHITE);
                massGraph.addSeries(massSeries);
            }

            if (graphDraw != null && graphDraw.getString("eated").equals("true")) {
                LineGraphSeries<DataPoint> eatedSeries = new LineGraphSeries<>(eatedData);
                StaticLabelsFormatter eatedLabelsFormatter = new StaticLabelsFormatter(eatedCaloriesGraph);
                eatedLabelsFormatter.setHorizontalLabels(graphHor);
                eatedCaloriesGraph.getGridLabelRenderer().setLabelFormatter(eatedLabelsFormatter);
                eatedSeries.setColor(Color.WHITE);
                eatedCaloriesGraph.addSeries(eatedSeries);
            }

            if (graphDraw != null && graphDraw.getString("bern").equals("true")) {
                LineGraphSeries<DataPoint> bernSeries = new LineGraphSeries<>(bernData);
                StaticLabelsFormatter bernLabelsFormatter = new StaticLabelsFormatter(bernCaloriesGraph);
                bernLabelsFormatter.setHorizontalLabels(graphHor);
                bernCaloriesGraph.getGridLabelRenderer().setLabelFormatter(bernLabelsFormatter);
                bernSeries.setColor(Color.WHITE);
                bernCaloriesGraph.addSeries(bernSeries);
            }

            if (graphDraw != null && graphDraw.getString("shoulders").equals("true")) {
                LineGraphSeries<DataPoint> shouldersSeries = new LineGraphSeries<>(shouldersData);
                StaticLabelsFormatter shouldersLabelsFormatter = new StaticLabelsFormatter(shouldersGraph);
                shouldersLabelsFormatter.setHorizontalLabels(graphHor);
                shouldersGraph.getGridLabelRenderer().setLabelFormatter(shouldersLabelsFormatter);
                shouldersSeries.setColor(Color.WHITE);
                shouldersGraph.addSeries(shouldersSeries);
            }

            if (graphDraw != null && graphDraw.getString("hands").equals("true")) {
                LineGraphSeries<DataPoint> rHandSeries = new LineGraphSeries<>(rHandData);
                StaticLabelsFormatter rHandLabelsFormatter = new StaticLabelsFormatter(rHandGraph);
                rHandLabelsFormatter.setHorizontalLabels(graphHor);
                rHandGraph.getGridLabelRenderer().setLabelFormatter(rHandLabelsFormatter);
                rHandSeries.setColor(Color.WHITE);
                rHandGraph.addSeries(rHandSeries);

                LineGraphSeries<DataPoint> lHandSeries = new LineGraphSeries<>(lHandData);
                StaticLabelsFormatter lHandLabelsFormatter = new StaticLabelsFormatter(lHandGraph);
                lHandLabelsFormatter.setHorizontalLabels(graphHor);
                lHandGraph.getGridLabelRenderer().setLabelFormatter(lHandLabelsFormatter);
                lHandSeries.setColor(Color.WHITE);
                lHandGraph.addSeries(lHandSeries);
            }

            if (graphDraw != null && graphDraw.getString("chest").equals("true")) {
                LineGraphSeries<DataPoint> chestSeries = new LineGraphSeries<>(chestData);
                StaticLabelsFormatter chestLabelsFormatter = new StaticLabelsFormatter(chestGraph);
                chestLabelsFormatter.setHorizontalLabels(graphHor);
                chestGraph.getGridLabelRenderer().setLabelFormatter(chestLabelsFormatter);
                chestSeries.setColor(Color.WHITE);
                chestGraph.addSeries(chestSeries);
            }

            if (graphDraw != null && graphDraw.getString("waist").equals("true")) {
                LineGraphSeries<DataPoint> waistSeries = new LineGraphSeries<>(waistData);
                StaticLabelsFormatter waistLabelsFormatter = new StaticLabelsFormatter(waistGraph);
                waistLabelsFormatter.setHorizontalLabels(graphHor);
                waistGraph.getGridLabelRenderer().setLabelFormatter(waistLabelsFormatter);
                waistSeries.setColor(Color.WHITE);
                waistGraph.addSeries(waistSeries);
            }

            if (graphDraw != null && graphDraw.getString("butt").equals("true")) {
                LineGraphSeries<DataPoint> buttSeries = new LineGraphSeries<>(buttData);
                StaticLabelsFormatter buttLabelsFormatter = new StaticLabelsFormatter(buttGraph);
                buttLabelsFormatter.setHorizontalLabels(graphHor);
                buttGraph.getGridLabelRenderer().setLabelFormatter(buttLabelsFormatter);
                buttSeries.setColor(Color.WHITE);
                buttGraph.addSeries(buttSeries);

            }

            if (graphDraw != null && graphDraw.getString("legs").equals("true")) {
                LineGraphSeries<DataPoint> rLegSeries = new LineGraphSeries<>(rLegData);
                StaticLabelsFormatter rLegLabelsFormatter = new StaticLabelsFormatter(rLegGraph);
                rLegLabelsFormatter.setHorizontalLabels(graphHor);
                rLegGraph.getGridLabelRenderer().setLabelFormatter(rLegLabelsFormatter);
                rLegSeries.setColor(Color.WHITE);
                rLegGraph.addSeries(rLegSeries);

                LineGraphSeries<DataPoint> lLegSeries = new LineGraphSeries<>(lLegData);
                StaticLabelsFormatter lLegLabelsFormatter = new StaticLabelsFormatter(lLegGraph);
                lLegLabelsFormatter.setHorizontalLabels(graphHor);
                lLegGraph.getGridLabelRenderer().setLabelFormatter(lLegLabelsFormatter);
                lLegSeries.setColor(Color.WHITE);
                lLegGraph.addSeries(lLegSeries);
            }


            if (graphDraw != null && graphDraw.getString("calves").equals("true")) {
                LineGraphSeries<DataPoint> calvesSeries = new LineGraphSeries<>(calvesData);
                StaticLabelsFormatter calvesLabelsFormatter = new StaticLabelsFormatter(calvesGraph);
                calvesLabelsFormatter.setHorizontalLabels(graphHor);
                calvesGraph.getGridLabelRenderer().setLabelFormatter(calvesLabelsFormatter);
                calvesSeries.setColor(Color.WHITE);
                calvesGraph.addSeries(calvesSeries);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initRows(){
        rowMass.setBackgroundResource(R.drawable.table_border_gray);
        rowDayLabels.setBackgroundResource(R.drawable.table_border_white);
        rowBurnCalories.setBackgroundResource(R.drawable.table_border_gray);
        rowEatedCalories.setBackgroundResource(R.drawable.table_border_white);
        rowShoulders.setBackgroundResource(R.drawable.table_border_white);
        rowLHand.setBackgroundResource(R.drawable.table_border_white);
        rowRHand.setBackgroundResource(R.drawable.table_border_gray);
        rowRLeg.setBackgroundResource(R.drawable.table_border_white);
        rowLLeg.setBackgroundResource(R.drawable.table_border_gray);
        rowWaist.setBackgroundResource(R.drawable.table_border_white);
        rowChest.setBackgroundResource(R.drawable.table_border_gray);
        rowButt.setBackgroundResource(R.drawable.table_border_gray);
        rowCalves.setBackgroundResource(R.drawable.table_border_white);


        int padding_in_dp = 14;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        rowDayLabels.setPadding(0,padding_in_px,0,padding_in_px);
        rowMass.setPadding(0,padding_in_px,0,padding_in_px);
        rowEatedCalories.setPadding(0,padding_in_px,0,padding_in_px);
        rowBurnCalories.setPadding(0,padding_in_px,0,padding_in_px);
        rowShoulders.setPadding(0,padding_in_px,0,padding_in_px);
        rowRHand.setPadding(0,padding_in_px,0,padding_in_px);
        rowLHand.setPadding(0,padding_in_px,0,padding_in_px);
        rowChest.setPadding(0,padding_in_px,0,padding_in_px);
        rowWaist.setPadding(0,padding_in_px,0,padding_in_px);
        rowButt.setPadding(0,padding_in_px,0,padding_in_px);
        rowRLeg.setPadding(0,padding_in_px,0,padding_in_px);
        rowLLeg.setPadding(0,padding_in_px,0,padding_in_px);
        rowCalves.setPadding(0,padding_in_px,0,padding_in_px);
    }

    private void setRowData(){
        try {
            JSONObject jsn;
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                tableDataArray = jsn.getJSONArray("days");
                jsn.remove("days");
            }
            int padding_in_dp = 9;  // 6 dps
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

            if(tableDataArray!=null && tableDataArray.length()>0) {
                for (int i = 0; i < tableDataArray.length() + 1; i++) {
                    if (i == 0) {
                        TextView dayLabel = new TextView(getApplicationContext());
                        dayLabel.setText("Дата");
                        dayLabel.setPadding(padding_in_px, 0, 0, 0);
                        dayLabel.setTextColor(Color.GRAY);
                        dayLabel.setTextSize(18f);
                        dayLabel.setGravity(Gravity.CENTER_VERTICAL);


                        TextView dayMass = new TextView(getApplicationContext());
                        dayMass.setText("Масса");
                        dayMass.setPadding(padding_in_px, 0, 0, 0);
                        dayMass.setTextColor(Color.BLACK);
                        dayMass.setTextSize(18f);
                        dayMass.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayEated = new TextView(getApplicationContext());
                        dayEated.setText("Потреблено");
                        dayEated.setPadding(padding_in_px, 0, 0, 0);
                        dayEated.setTextColor(Color.BLACK);
                        dayEated.setTextSize(18f);
                        dayEated.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayBurn = new TextView(getApplicationContext());
                        dayBurn.setText("Затрачено");
                        dayBurn.setPadding(padding_in_px, 0, 0, 0);
                        dayBurn.setTextColor(Color.BLACK);
                        dayBurn.setTextSize(18f);
                        dayBurn.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayShoulders = new TextView(getApplicationContext());
                        dayShoulders.setText("Плечи");
                        dayShoulders.setPadding(padding_in_px, 0, 0, 0);
                        dayShoulders.setTextColor(Color.BLACK);
                        dayShoulders.setTextSize(18f);
                        dayShoulders.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayRHand = new TextView(getApplicationContext());
                        dayRHand.setText("Пр. рука");
                        dayRHand.setPadding(padding_in_px, 0, 0, 0);
                        dayRHand.setTextColor(Color.BLACK);
                        dayRHand.setTextSize(18f);
                        dayRHand.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayLHand = new TextView(getApplicationContext());
                        dayLHand.setText("Лев. Рука");
                        dayLHand.setPadding(padding_in_px, 0, 0, 0);
                        dayLHand.setTextColor(Color.BLACK);
                        dayLHand.setTextSize(18f);
                        dayLHand.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayChest = new TextView(getApplicationContext());
                        dayChest.setText("Грудь");
                        dayChest.setPadding(padding_in_px, 0, 0, 0);
                        dayChest.setTextColor(Color.BLACK);
                        dayChest.setTextSize(18f);
                        dayChest.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayWaist = new TextView(getApplicationContext());
                        dayWaist.setText("Талия");
                        dayWaist.setPadding(padding_in_px, 0, 0, 0);
                        dayWaist.setTextColor(Color.BLACK);
                        dayWaist.setTextSize(18f);
                        dayWaist.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayButt = new TextView(getApplicationContext());
                        dayButt.setText("Ягодицы");
                        dayButt.setPadding(padding_in_px, 0, 0, 0);
                        dayButt.setTextColor(Color.BLACK);
                        dayButt.setTextSize(18f);
                        dayButt.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayRLeg = new TextView(getApplicationContext());
                        dayRLeg.setText("Пр. бедро");
                        dayRLeg.setPadding(padding_in_px, 0, 0, 0);
                        dayRLeg.setTextColor(Color.BLACK);
                        dayRLeg.setTextSize(18f);
                        dayRLeg.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayLLeg = new TextView(getApplicationContext());
                        dayLLeg.setText("Лев. бедро");
                        dayLLeg.setPadding(padding_in_px, 0, 0, 0);
                        dayLLeg.setTextColor(Color.BLACK);
                        dayLLeg.setTextSize(18f);
                        dayLLeg.setGravity(Gravity.CENTER_VERTICAL);

                        TextView dayCalves = new TextView(getApplicationContext());
                        dayCalves.setText("Икры");
                        dayCalves.setPadding(padding_in_px, 0, 0, 0);
                        dayCalves.setTextColor(Color.BLACK);
                        dayCalves.setTextSize(18f);
                        dayCalves.setGravity(Gravity.CENTER_VERTICAL);

                        rowDayLabels.addView(dayLabel);
                        rowMass.addView(dayMass);
                        rowEatedCalories.addView(dayEated);
                        rowBurnCalories.addView(dayBurn);
                        rowShoulders.addView(dayShoulders);
                        rowRHand.addView(dayRHand);
                        rowLHand.addView(dayLHand);
                        rowChest.addView(dayChest);
                        rowWaist.addView(dayWaist);
                        rowButt.addView(dayButt);
                        rowRLeg.addView(dayRLeg);
                        rowLLeg.addView(dayLLeg);
                        rowCalves.addView(dayCalves);


                    } else {
                        TextView dayLabel = new TextView(getApplicationContext());
                        String date = tableDataArray.getJSONObject(i - 1).getString("date").replace('-','.');
                        dayLabel.setText(date.substring(date.indexOf('.')).substring(date.indexOf('.'))+
                                date.substring(date.indexOf('.'), date.indexOf('.')+3)+"."+date.substring(0,4));
                        dayLabel.setTextSize(18f);
                        dayLabel.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayLabel.setTextColor(Color.GRAY);
                        dayLabel.setGravity(Gravity.END);

                        TextView dayMass = new TextView(getApplicationContext());
                        dayMass.setText(tableDataArray.getJSONObject(i - 1).getString("mass") + " кг");
                        dayMass.setTextColor(Color.parseColor("#1D1D1D"));
                        dayMass.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayMass.setTextSize(18f);
                        dayMass.setGravity(Gravity.END);

                        TextView dayEated = new TextView(getApplicationContext());
                        dayEated.setText(tableDataArray.getJSONObject(i - 1).getString("food_sum") + " ккал");
                        dayEated.setTextColor(Color.parseColor("#1D1D1D"));
                        dayEated.setTextSize(18f);
                        dayEated.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayEated.setGravity(Gravity.END);

                        TextView dayBurn = new TextView(getApplicationContext());
                        dayBurn.setText(tableDataArray.getJSONObject(i - 1).getString("active_sum") + " ккал");
                        dayBurn.setTextColor(Color.parseColor("#1D1D1D"));
                        dayBurn.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayBurn.setTextSize(18f);
                        dayBurn.setGravity(Gravity.END);

                        TextView dayShoulders = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("shoulders").equals("0"))
                            dayShoulders.setText(tableDataArray.getJSONObject(i - 1).getString("shoulders") + " см");
                        else dayShoulders.setText("-");
                        dayShoulders.setTextColor(Color.parseColor("#1D1D1D"));
                        dayShoulders.setTextSize(18f);
                        dayShoulders.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayShoulders.setGravity(Gravity.END);

                        TextView dayRHand = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("rHand").equals("0"))
                            dayRHand.setText(tableDataArray.getJSONObject(i - 1).getString("rHand") + " см");
                        else dayRHand.setText("-");
                        dayRHand.setTextColor(Color.parseColor("#1D1D1D"));
                        dayRHand.setTextSize(18f);
                        dayRHand.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayRHand.setGravity(Gravity.END);

                        TextView dayLHand = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("lHand").equals("0"))
                            dayLHand.setText(tableDataArray.getJSONObject(i - 1).getString("lHand") + " см");
                        else dayLHand.setText("-");
                        dayLHand.setTextColor(Color.parseColor("#1D1D1D"));
                        dayLHand.setTextSize(18f);
                        dayLHand.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayLHand.setGravity(Gravity.END);

                        TextView dayChest = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("chest").equals("0"))
                            dayChest.setText(tableDataArray.getJSONObject(i - 1).getString("chest") + " см");
                        else dayChest.setText("-");
                        dayChest.setTextColor(Color.parseColor("#1D1D1D"));
                        dayChest.setTextSize(18f);
                        dayChest.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayChest.setGravity(Gravity.END);

                        TextView dayWaist = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("waist").equals("0"))
                            dayWaist.setText(tableDataArray.getJSONObject(i - 1).getString("waist") + " см");
                        else dayWaist.setText("-");
                        dayWaist.setTextColor(Color.parseColor("#1D1D1D"));
                        dayWaist.setTextSize(18f);
                        dayWaist.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayWaist.setGravity(Gravity.END);

                        TextView dayButt = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("butt").equals("0"))
                            dayButt.setText(tableDataArray.getJSONObject(i - 1).getString("butt") + " см");
                        else dayButt.setText("-");
                        dayButt.setTextColor(Color.parseColor("#1D1D1D"));
                        dayButt.setTextSize(18f);
                        dayButt.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayButt.setGravity(Gravity.END);

                        TextView dayRLeg = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("rLeg").equals("0"))
                            dayRLeg.setText(tableDataArray.getJSONObject(i - 1).getString("rLeg") + " см");
                        else dayRLeg.setText("-");
                        dayRLeg.setTextColor(Color.parseColor("#1D1D1D"));
                        dayRLeg.setTextSize(18f);
                        dayRLeg.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayRLeg.setGravity(Gravity.END);

                        TextView dayLLeg = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("lLeg").equals("0"))
                            dayLLeg.setText(tableDataArray.getJSONObject(i - 1).getString("lLeg") + " см");
                        else dayLLeg.setText("-");
                        dayLLeg.setTextColor(Color.parseColor("#1D1D1D"));
                        dayLLeg.setTextSize(18f);
                        dayLLeg.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayLLeg.setGravity(Gravity.END);

                        TextView dayCalves = new TextView(getApplicationContext());
                        if(!tableDataArray.getJSONObject(i - 1).getString("calves").equals("0"))
                            dayCalves.setText(tableDataArray.getJSONObject(i - 1).getString("calves") + " см");
                        else dayCalves.setText("-");
                        dayCalves.setTextColor(Color.parseColor("#1D1D1D"));
                        dayCalves.setTextSize(18f);
                        dayCalves.setPadding(padding_in_px, 0, padding_in_px, 0);
                        dayCalves.setGravity(Gravity.RIGHT);

                        rowDayLabels.addView(dayLabel);
                        rowMass.addView(dayMass);
                        rowEatedCalories.addView(dayEated);
                        rowBurnCalories.addView(dayBurn);
                        rowShoulders.addView(dayShoulders);
                        rowRHand.addView(dayRHand);
                        rowLHand.addView(dayLHand);
                        rowChest.addView(dayChest);
                        rowWaist.addView(dayWaist);
                        rowButt.addView(dayButt);
                        rowRLeg.addView(dayRLeg);
                        rowLLeg.addView(dayLLeg);
                        rowCalves.addView(dayCalves);
                    }
                }
                table.addView(rowDayLabels);
                table.addView(rowMass);
                table.addView(rowEatedCalories);
                table.addView(rowBurnCalories);
                table.addView(rowShoulders);
                table.addView(rowRHand);
                table.addView(rowLHand);
                table.addView(rowChest);
                table.addView(rowWaist);
                table.addView(rowButt);
                table.addView(rowRLeg);
                table.addView(rowLLeg);
                table.addView(rowCalves);
            }
            else{
                Toast.makeText(getApplicationContext(), "Отсутствуют данные для отображения", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG ).show();
        }
    }

    public void initTabs()
    {
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup();
        TabHost.TabSpec tabSpec;

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("tag1");
        // название вкладки
        tabSpec.setIndicator("Графики");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.tab1);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");

        tabSpec.setIndicator("Таблица");

        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        for(int i=0; i<tabWidget.getChildCount(); i++){
            final ViewGroup tab = (ViewGroup) tabWidget.getChildAt(i);
            final TextView tabTextView = (TextView) tab.getChildAt(1); // Magic number
            tabTextView.setTextColor(Color.WHITE);
        }

//        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            public void onTabChanged(String tabId) {
//                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
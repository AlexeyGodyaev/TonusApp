package com.caloriesdiary.caloriesdiary;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class StatActivity extends AppCompatActivity {
    GraphView massGraph, eatedCaloriesGraph, bernCaloriesGraph, rLegGraph, lLegGraph, calvesGraph, buttGraph, rHandGraph,
            lHandGraph, chestGraph, shouldersGraph, waistGraph;
    JSONArray graphArr=null;
    DataPoint massData [], eatedData[], bernData [], rLegData [], lLegData [], rHandData [],
    lHandData [], waistData [], chestData [], buttData [], shouldersData [], calvesData [];
    String graphHor [];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_layout);

        setTitle("Статистика");

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

        massGraph.setTitle("Масса");
        eatedCaloriesGraph.setTitle("Потребляемые калории");
        bernCaloriesGraph.setTitle("Затраченные калории");
        shouldersGraph.setTitle("Плечи");
        waistGraph.setTitle("Талия");
        chestGraph.setTitle("Грудь");
        buttGraph.setTitle("Ягодицы");
        rLegGraph.setTitle("Правое бедро");
        lLegGraph.setTitle("Левое бедро");
        rHandGraph.setTitle("Правая рука");
        lHandGraph.setTitle("Левая рука");
        calvesGraph.setTitle("Икры");

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

                for (int i = 0; i < graphArr.length(); i++) {
                    massData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("mass")));
                    eatedData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("eatedCalories")));
                    bernData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("bernCalories")));
                    rLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rLeg")));
                    lLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lLeg")));
                    rHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rHand")));
                    lHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lHand")));
                    waistData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("waist")));
                    chestData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("chest")));
                    shouldersData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("shoulders")));
                    buttData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("butt")));
                    calvesData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("calves")));
                    graphHor[i] = graphArr.getJSONObject(i).getString("date");
                }

                LineGraphSeries<DataPoint> massSeries = new LineGraphSeries<>(massData);
                StaticLabelsFormatter massLabelsFormatter = new StaticLabelsFormatter(massGraph);
                massLabelsFormatter.setHorizontalLabels(graphHor);
                massGraph.getGridLabelRenderer().setLabelFormatter(massLabelsFormatter);

                massGraph.addSeries(massSeries);

                LineGraphSeries<DataPoint> eatedSeries = new LineGraphSeries<>(eatedData);
                StaticLabelsFormatter eatedLabelsFormatter = new StaticLabelsFormatter(eatedCaloriesGraph);
                eatedLabelsFormatter.setHorizontalLabels(graphHor);
                eatedCaloriesGraph.getGridLabelRenderer().setLabelFormatter(eatedLabelsFormatter);

                eatedCaloriesGraph.addSeries(eatedSeries);

                LineGraphSeries<DataPoint> bernSeries = new LineGraphSeries<>(bernData);
                StaticLabelsFormatter bernLabelsFormatter = new StaticLabelsFormatter(bernCaloriesGraph);
                bernLabelsFormatter.setHorizontalLabels(graphHor);
                bernCaloriesGraph.getGridLabelRenderer().setLabelFormatter(bernLabelsFormatter);

                bernCaloriesGraph.addSeries(bernSeries);

                LineGraphSeries<DataPoint> shouldersSeries = new LineGraphSeries<>(shouldersData);
                StaticLabelsFormatter shouldersLabelsFormatter = new StaticLabelsFormatter(shouldersGraph);
                shouldersLabelsFormatter.setHorizontalLabels(graphHor);
                shouldersGraph.getGridLabelRenderer().setLabelFormatter(shouldersLabelsFormatter);

                shouldersGraph.addSeries(shouldersSeries);

                LineGraphSeries<DataPoint> waistSeries = new LineGraphSeries<>(waistData);
                StaticLabelsFormatter waistLabelsFormatter = new StaticLabelsFormatter(waistGraph);
                waistLabelsFormatter.setHorizontalLabels(graphHor);
                waistGraph.getGridLabelRenderer().setLabelFormatter(waistLabelsFormatter);

                waistGraph.addSeries(waistSeries);

                LineGraphSeries<DataPoint> chestSeries = new LineGraphSeries<>(chestData);
                StaticLabelsFormatter chestLabelsFormatter = new StaticLabelsFormatter(chestGraph);
                chestLabelsFormatter.setHorizontalLabels(graphHor);
                chestGraph.getGridLabelRenderer().setLabelFormatter(chestLabelsFormatter);

                chestGraph.addSeries(chestSeries);

                LineGraphSeries<DataPoint> buttSeries = new LineGraphSeries<>(buttData);
                StaticLabelsFormatter buttLabelsFormatter = new StaticLabelsFormatter(buttGraph);
                buttLabelsFormatter.setHorizontalLabels(graphHor);
                buttGraph.getGridLabelRenderer().setLabelFormatter(buttLabelsFormatter);

                buttGraph.addSeries(buttSeries);

                LineGraphSeries<DataPoint> rLegSeries = new LineGraphSeries<>(rLegData);
                StaticLabelsFormatter rLegLabelsFormatter = new StaticLabelsFormatter(rLegGraph);
                rLegLabelsFormatter.setHorizontalLabels(graphHor);
                rLegGraph.getGridLabelRenderer().setLabelFormatter(rLegLabelsFormatter);

                rLegGraph.addSeries(rLegSeries);

                LineGraphSeries<DataPoint> lLegSeries = new LineGraphSeries<>(lLegData);
                StaticLabelsFormatter lLegLabelsFormatter = new StaticLabelsFormatter(lLegGraph);
                lLegLabelsFormatter.setHorizontalLabels(graphHor);
                lLegGraph.getGridLabelRenderer().setLabelFormatter(lLegLabelsFormatter);

                lLegGraph.addSeries(lLegSeries);

                LineGraphSeries<DataPoint> rHandSeries = new LineGraphSeries<>(rHandData);
                StaticLabelsFormatter rHandLabelsFormatter = new StaticLabelsFormatter(rHandGraph);
                rHandLabelsFormatter.setHorizontalLabels(graphHor);
                rHandGraph.getGridLabelRenderer().setLabelFormatter(rHandLabelsFormatter);

                rHandGraph.addSeries(rHandSeries);

                LineGraphSeries<DataPoint> lHandSeries = new LineGraphSeries<>(lHandData);
                StaticLabelsFormatter lHandLabelsFormatter = new StaticLabelsFormatter(lHandGraph);
                lHandLabelsFormatter.setHorizontalLabels(graphHor);
                lHandGraph.getGridLabelRenderer().setLabelFormatter(lHandLabelsFormatter);

                lHandGraph.addSeries(lHandSeries);

                LineGraphSeries<DataPoint> calvesSeries = new LineGraphSeries<>(calvesData);
                StaticLabelsFormatter calvesLabelsFormatter = new StaticLabelsFormatter(calvesGraph);
                calvesLabelsFormatter.setHorizontalLabels(graphHor);
                calvesGraph.getGridLabelRenderer().setLabelFormatter(calvesLabelsFormatter);

                calvesGraph.addSeries(calvesSeries);
            }
            else Toast.makeText(this, "Статистика доступна после двух заполненных дней", Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}

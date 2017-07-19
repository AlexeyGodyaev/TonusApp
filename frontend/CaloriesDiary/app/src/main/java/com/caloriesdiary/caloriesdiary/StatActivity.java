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
    GraphView massGraph, eatedCaloriesGraph, bernCaloriesGraph, rLegGrapg, lLegGraph, calvesGraph, buttGraph, rHandGraph,
            lHandGraph, chestGraph, shouldersGraph, waistGraph;
    JSONArray graphArr;
    DataPoint massData [], eatedData[], bernData [];
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

        try {
            massData = new DataPoint[graphArr.length()];
            eatedData = new DataPoint[graphArr.length()];
            bernData = new DataPoint[graphArr.length()];
            graphHor = new String[graphArr.length()];

            for (int i = 0; i < graphArr.length(); i++) {
                massData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("mass")));
                eatedData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("eatedCalories")));
                bernData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("bernCalories")));
                graphHor[i] = graphArr.getJSONObject(i).getString("date");
            }

            LineGraphSeries<DataPoint> massSeries = new LineGraphSeries<>(massData);
            StaticLabelsFormatter massLabelsFormatter = new StaticLabelsFormatter(massGraph);
            massLabelsFormatter.setHorizontalLabels(graphHor);
            massGraph.getGridLabelRenderer().setLabelFormatter(massLabelsFormatter);

            massGraph.addSeries(massSeries);
            massGraph.setTitle("Масса");

            LineGraphSeries<DataPoint> eatedSeries = new LineGraphSeries<>(eatedData);
            StaticLabelsFormatter eatedLabelsFormatter = new StaticLabelsFormatter(eatedCaloriesGraph);
            eatedLabelsFormatter.setHorizontalLabels(graphHor);
            eatedCaloriesGraph.getGridLabelRenderer().setLabelFormatter(eatedLabelsFormatter);

            eatedCaloriesGraph.addSeries(eatedSeries);
            eatedCaloriesGraph.setTitle("Потребляемые калории");

            LineGraphSeries<DataPoint> bernSeries = new LineGraphSeries<>(bernData);
            StaticLabelsFormatter bernLabelsFormatter = new StaticLabelsFormatter(bernCaloriesGraph);
            bernLabelsFormatter.setHorizontalLabels(graphHor);
            bernCaloriesGraph.getGridLabelRenderer().setLabelFormatter(bernLabelsFormatter);

            bernCaloriesGraph.addSeries(bernSeries);
            bernCaloriesGraph.setTitle("Затраченные калории");
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

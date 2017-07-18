package com.caloriesdiary.caloriesdiary;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class StatActivity extends AppCompatActivity {
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_layout);

        setTitle("Статистика");

        graphView = (GraphView) findViewById(R.id.mass_graph_container);

        LineGraphSeries<DataPoint> exampleSeries = new LineGraphSeries<>(
                new DataPoint[] { new DataPoint(1, 3.0d),
                        //new DataPoint(2, 1.5d), new DataPoint(3, 2.5d),
                        new DataPoint(4, 1.0d), new DataPoint(5, 1.3d) });

        graphView.addSeries(exampleSeries);
    }
}

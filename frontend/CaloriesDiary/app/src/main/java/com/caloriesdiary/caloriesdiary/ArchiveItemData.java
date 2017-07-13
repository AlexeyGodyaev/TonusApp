package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;
import android.widget.TextView;

public class ArchiveItemData extends AppCompatActivity{
    TextView goalName, goalType, desiredWeight, goalState, beginDate, endDate, liveType;
    String goalTypeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_item_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("ПРОСМОТР ЦЕЛИ");


        goalState = (TextView) findViewById(R.id.state_count);
        goalName = (TextView) findViewById(R.id.archive_goal_name_text);
        goalName.setText(getIntent().getStringExtra("name"));
        goalType = (TextView) findViewById(R.id.archive_goal_type_text);
        switch (Integer.parseInt(getIntent().getStringExtra("goal"))){
            case 1:
                goalTypeText = "Снижение массы подкожного жира";
                break;

            case 2:
                goalTypeText = "Снизить общую массу тела";
                break;

            case 3:
                goalTypeText = "Набрать мышечную массу, безжировую";
                break;

            case 4:
                goalTypeText = "Набрать общую массу тела";
                break;

            case 5:
                goalTypeText = "Сохранение кондиций тела при смене деятельности";
                break;
        }
        goalType.setText(goalTypeText);
        beginDate = (TextView) findViewById(R.id.archive_begin_date_text);
        beginDate.setText(getIntent().getStringExtra("begin_date"));
        endDate = (TextView) findViewById(R.id.archive_end_date_text);
        desiredWeight = (TextView) findViewById(R.id.archive_weight_text);
        desiredWeight.setText(getIntent().getStringExtra("desired_weight"));
        liveType = (TextView) findViewById(R.id.live_type_text);

    }
}

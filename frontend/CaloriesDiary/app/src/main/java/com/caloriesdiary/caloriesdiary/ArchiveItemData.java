package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class ArchiveItemData extends AppCompatActivity{
    TextView goalName, goalType, desiredWeight, goalState, beginDate, endDate, liveType,  antropometryText;
    String typeText;
    boolean flag = true;
    private ArchiveAntropometryFragment fragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_item_data);

        manager = getSupportFragmentManager();
        fragment = new ArchiveAntropometryFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("ПРОСМОТР ЦЕЛИ");

        antropometryText = (TextView) findViewById(R.id.archive_antropometry_text);
        goalState = (TextView) findViewById(R.id.state_count);
        switch (getIntent().getStringExtra("state")){
            case "false":
                typeText = "Не выполнено";
                break;
            case "true":
                typeText = "Выполнено";
                break;
        }
        goalState.setText(typeText);
        goalName = (TextView) findViewById(R.id.archive_goal_name_text);
        goalName.setText(getIntent().getStringExtra("name"));
        goalType = (TextView) findViewById(R.id.archive_goal_type_text);
        switch (Integer.parseInt(getIntent().getStringExtra("goal"))){
            case 1:
                typeText = "Снижение массы подкожного жира";
                break;

            case 2:
                typeText = "Снизить общую массу тела";
                break;

            case 3:
                typeText = "Набрать мышечную массу, безжировую";
                break;

            case 4:
                typeText = "Набрать общую массу тела";
                break;

            case 5:
                typeText = "Сохранение кондиций тела при смене деятельности";
                break;
        }
        goalType.setText(typeText);
        beginDate = (TextView) findViewById(R.id.archive_begin_date_text);
        beginDate.setText(getIntent().getStringExtra("begin_date"));
        endDate = (TextView) findViewById(R.id.archive_end_date_text);
        endDate.setText(getIntent().getStringExtra("endDate"));
        desiredWeight = (TextView) findViewById(R.id.archive_weight_text);
        desiredWeight.setText(getIntent().getStringExtra("desired_weight"));
        liveType = (TextView) findViewById(R.id.live_type_text);
        switch (Integer.parseInt(getIntent().getStringExtra("active"))){
            case 1:
                typeText = "Практически нет";
                break;

            case 2:
                typeText = "Легкий (2-3 дня в неделю)";
                break;

            case 3:
                typeText = "Умеренный (3-5 дней в неделю)";
                break;

            case 4:
                typeText = "Тяжелый (6-7 дней в неделю)";
                break;

            case 5:
                typeText = "Очень тяжелый (Тренировки по два раза в день)";
                break;
        }
        liveType.setText(typeText);
    }

    public void onAntropometryClc(View view){
        transaction = manager.beginTransaction();

        if(flag){
            transaction.add(R.id.antropometry_archive, fragment); flag = false;

            }
        else {transaction.remove(fragment); flag = true;}

        transaction.commit();
    }

}

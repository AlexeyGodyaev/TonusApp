package com.caloriesdiary.caloriesdiary;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SetAGoal extends AppCompatActivity {
    EditText editGoalName, editWeight, editDaysCount;
    TextView test;
    Spinner goalType, liveType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_a_goal_layout);

        goalType = (Spinner) findViewById(R.id.typeSpinner);
        liveType = (Spinner) findViewById(R.id.liveTypeSpinner);
        editGoalName = (EditText) findViewById(R.id.editGoal);
        editWeight = (EditText) findViewById(R.id.editWeight);
        editDaysCount = (EditText) findViewById(R.id.editDaysCount);


    }

    public void onStartRoadToGoal(View view){
        Post post = new Post();
        String s[] = new String[6];
        s[0] = "link"; s[1] = "напишешь id тут"; s[2] = editWeight.getText().toString(); s[3] = editDaysCount.getText().toString();
        s[4] = String.valueOf(liveType.getSelectedItemPosition()+1); s[5] = String.valueOf(goalType.getSelectedItemPosition()+1);

        post.execute(s);

        Intent intent = new Intent(this, TodayActivity.class);
        startActivity(intent);
    }
}

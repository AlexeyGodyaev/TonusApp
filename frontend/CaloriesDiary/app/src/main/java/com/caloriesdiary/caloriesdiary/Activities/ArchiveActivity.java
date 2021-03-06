package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Adapters.ArchiveAdapter;
import com.caloriesdiary.caloriesdiary.Items.ArchiveItem;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.R;
import com.caloriesdiary.caloriesdiary.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArchiveActivity extends AppCompatActivity {

    SharedPreferences sharedPref = null;
    String endDate [];
    JSONArray jArr;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);

        setTitle("АРХИВ");

        final LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);

        mRecyclerView =  (RecyclerView) findViewById(R.id.archive_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ArchiveAdapter(initData());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Intent intent = new Intent(getApplicationContext(), ArchiveItemDataActivity.class);

                try {
                    intent.putExtra("name", jArr.getJSONObject(position).getString("name"));
                    intent.putExtra("active", jArr.getJSONObject(position).getString("activityType"));
                    intent.putExtra("state", jArr.getJSONObject(position).getString("active"));
                    intent.putExtra("begin_date", jArr.getJSONObject(position).getString("begin_date"));
                    intent.putExtra("endDate", endDate[position]);
                    intent.putExtra("desired_weight", jArr.getJSONObject(position).getString("desired_weight"));
                    intent.putExtra("goal", jArr.getJSONObject(position).getString("goal"));
                    intent.putExtra("rLeg", jArr.getJSONObject(position).getString("right_thigh"));
                    intent.putExtra("lLeg", jArr.getJSONObject(position).getString("left_thigh"));
                    intent.putExtra("rHand", jArr.getJSONObject(position).getString("right_hand"));
                    intent.putExtra("lHand", jArr.getJSONObject(position).getString("left_hand"));
                    intent.putExtra("waist", jArr.getJSONObject(position).getString("waist"));
                    intent.putExtra("chest", jArr.getJSONObject(position).getString("breast"));
                    intent.putExtra("calves", jArr.getJSONObject(position).getString("calfs"));
                    intent.putExtra("butt", jArr.getJSONObject(position).getString("hiney"));
                    intent.putExtra("mass", jArr.getJSONObject(position).getString("weight"));
                    intent.putExtra("shoulders", jArr.getJSONObject(position).getString("shoulders"));
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(ArchiveActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public  String getAction() throws InterruptedException, ExecutionException {
        Post getArchive = new Post();
        String s [] = new String[2];
        s[0]="http://caloriesdiary.ru/users/get_goal_archive"; s[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
        getArchive.execute(s);

        return getArchive.get().toString();
    }

    private List<ArchiveItem> initData() {
        try {
            List<ArchiveItem> list = new ArrayList<>();
            String resp;
            String actionName, date;

            resp = getAction();

            if (resp != null) {
                JSONObject js = new JSONObject(resp);
                jArr = js.getJSONArray("userGoals");
                endDate = new String[jArr.length()];
                for (int i = 0; i < jArr.length(); i++) {

                    Calendar calendar = Calendar.getInstance();
                    endDate[i] = jArr.getJSONObject(i).getString("begin_date");
                    calendar.set(Calendar.YEAR, Integer.parseInt(endDate[i].substring(0, endDate[i].indexOf('-'))));

                    endDate[i] = endDate[i].substring(endDate[i].indexOf('-') + 1);
                    if (endDate[i].substring(0, 1).equals("0"))
                        endDate[i] = endDate[i].substring(1);
                    calendar.set(Calendar.MONTH, Integer.parseInt(endDate[i].substring(0, endDate[i].indexOf('-'))) - 1);

                    endDate[i] = endDate[i].substring(endDate[i].indexOf('-') + 1);
                    if (endDate[i].substring(0, 1).equals("0"))
                        endDate[i] = endDate[i].substring(1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDate[i]));

                    int end = calendar.get(Calendar.DAY_OF_YEAR) + Integer.parseInt(jArr.getJSONObject(i).getString("period"));
                    calendar.set(Calendar.DAY_OF_YEAR, end);

                    endDate[i] = String.valueOf(calendar.get(Calendar.YEAR)) + "-";
                    if (calendar.get(Calendar.MONTH) < 10)
                        endDate[i] += "0";
                    endDate[i] += String.valueOf(calendar.get(Calendar.MONTH)) + "-";
                    if (calendar.get(Calendar.DAY_OF_MONTH) < 9)
                        endDate[i] += "0";
                    endDate[i] += String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + 1);

                    actionName = jArr.getJSONObject(i).getString("name");
                    date = jArr.getJSONObject(i).getString("begin_date") + "/" + "\n" + endDate[i];
                    list.add(new ArchiveItem(actionName, date));
                }
            }
            return list;

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }



    }

package com.caloriesdiary.caloriesdiary;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.jar.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btn;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    TextView userName, userMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
        btn = (Button) findViewById(R.id.profile_btn);
        //id_text_view = (TextView) findViewById(R.id.id_text_view);
        sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
        editor = sharedPref.edit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        userMail = (TextView) v.findViewById(R.id.head_usermail_text);
        userName = (TextView) v.findViewById(R.id.head_username_text);
        userMail.setText(sharedPref.getString("userMail", "Нет данных"));
        userName.setText(sharedPref.getString("userName", "Нет данных"));


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            try
            {
                Post log = new Post();

                String args[] = new String[3];

                args[0] = "http://94.130.12.179/users/get_user_chars";  //аргументы для пост запроса
                args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));


                log.execute(args); // вызываем запрос
                JSONObject JSans = log.get();


                if(JSans.getString("status").equals("0")) {
                    Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                else if(JSans.getString("status").equals("1"))
                {
                    Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
                    startActivity(intent);
                }
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_rar) {
            Intent intent = new Intent(getApplicationContext(),ArchiveActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  void onTodayClc(View view){
        Intent intent = new Intent(getApplicationContext(),TodayActivity.class);

        startActivity(intent);
    }
public void onClc(View view){
    Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);

    startActivity(intent);
}

    public void onFoodCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),RecycleFoodCatalogActivity.class);

        startActivity(intent);
    }

    public void onStatClc(View view){
        Intent intent = new Intent(getApplicationContext(),StatActivity.class);

        startActivity(intent);
    }

    public void onActionsCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),RecycleActionCatalogActivity.class);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void onProfileClick(View view){
        try
        {
            Post log = new Post();

            String args[] = new String[3];

            args[0] = "http://94.130.12.179/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));


            log.execute(args); // вызываем запрос
            JSONObject JSans = log.get();


            if(JSans.getString("status").equals("0")) {
                Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
            else if(JSans.getString("status").equals("1"))
            {
                Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void onDiaryClick (View view)
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Важное сообщение!")
                    .setMessage("Покормите кота!")
                     .setCancelable(false)
                    .setNegativeButton("ОК, иду на кухню",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
//            InputStream inputStream = getApplicationContext().openFileInput("text.txt");
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                Toast.makeText(getApplicationContext(),stringBuilder.toString(),Toast.LENGTH_LONG).show();

        //    }

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                //Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                //Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_UP) :
                //Toast.makeText(getApplicationContext(),"3",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                //Toast.makeText(getApplicationContext(),"4",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                //Toast.makeText(getApplicationContext(),"5",Toast.LENGTH_SHORT).show();
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
}

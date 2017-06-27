package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btn;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn= (Button) findViewById(R.id.profile_btn);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();
        InitPreference();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.login_menu :
                Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
public void onClc(View view){
    Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);

    startActivity(intent);
}
    public void onProfileClick(View view){
        if(sharedPref.getString("IS_PROFILE_CREATED","ERROR").equals("FALSE")) {
            Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
            startActivity(intent);
        }
        else if(sharedPref.getString("IS_PROFILE_CREATED","ERROR").equals("TRUE"))
        {
            Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
            startActivity(intent);
        }
    }
    public void InitPreference()
    {

        if(sharedPref.getBoolean("IS_FIRST_LAUNCH",true))
        {
            Toast.makeText(getApplicationContext(),"Вы в приложении в первый раз ",Toast.LENGTH_LONG).show();
            editor.putBoolean("IS_FIRST_LAUNCH",false);
        }
        editor.putString("IS_PROFILE_CREATED","FALSE");
        editor.putInt("PROFILE_ID",0);
        editor.commit();
        Map<String, ?> allEntries = sharedPref.getAll();   //Увидеть все настройки
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}

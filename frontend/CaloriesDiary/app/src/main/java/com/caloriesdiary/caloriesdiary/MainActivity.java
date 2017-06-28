package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btn;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    TextView id_text_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn= (Button) findViewById(R.id.profile_btn);
        id_text_view = (TextView) findViewById(R.id.id_text_view);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();


        id_text_view.setText(String.valueOf(sharedPref.getInt("PROFILE_ID",0)));
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

    public void onFoodCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),FoodCatalogActivity.class);

        startActivity(intent);
    }

    public void onActionsCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),ActionsCatalogActivity.class);

        startActivity(intent);
    }



    public void onProfileClick(View view){
        if(!sharedPref.getBoolean("IS_PROFILE_CREATED",false)) {
            Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
            startActivity(intent);
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

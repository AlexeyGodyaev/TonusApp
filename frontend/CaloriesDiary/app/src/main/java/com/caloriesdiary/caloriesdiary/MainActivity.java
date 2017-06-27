package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn= (Button) findViewById(R.id.profile_btn);
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



    public void onProfileClick(View view){
        Intent intent = new Intent(getApplicationContext(),PersonalProfileActivity.class);

        startActivity(intent);
    }

}

package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.caloriesdiary.caloriesdiary.Activities.ActionsCatalogActivity;
import com.caloriesdiary.caloriesdiary.Activities.AuthorizationActivity;
import com.caloriesdiary.caloriesdiary.Activities.FoodCatalogActivity;
import com.caloriesdiary.caloriesdiary.R;


public class GuestMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_main);
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
    public void onGuestFoodCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),FoodCatalogActivity.class);

        startActivity(intent);
    }

    public void onGuestActionsCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),ActionsCatalogActivity.class);

        startActivity(intent);
    }
}

package com.caloriesdiary.caloriesdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AuthorizationActivity extends Activity {

    Button reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_layout);
        reg = (Button) findViewById(R.id.registrationButton);
    }

    public  void registrationClc(View view){
        Intent intent = new Intent(getApplicationContext(),RegistrationActivity.class);

        startActivity(intent);
    }
}

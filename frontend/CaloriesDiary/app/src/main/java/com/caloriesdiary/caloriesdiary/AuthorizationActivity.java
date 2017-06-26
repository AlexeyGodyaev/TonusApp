package com.caloriesdiary.caloriesdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;


public class AuthorizationActivity extends Activity {

    Button reg;
    Button logBtn;
    EditText login, pass;
    TextView err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_layout);
        reg = (Button) findViewById(R.id.registrationButton);
        logBtn = (Button) findViewById(R.id.loginButton);
        login = (EditText) findViewById(R.id.editLogin);
        pass = (EditText) findViewById(R.id.editPassword);
        err = (TextView) findViewById(R.id.testRequestText);

    }

    public  void registrationClc(View view){
        Intent intent = new Intent(getApplicationContext(),RegistrationActivity.class);

        startActivity(intent);
    }

    public void loginClc(View view) throws InterruptedException, ExecutionException {
        Post log = new Post();

        String args [] = new String[3];

        args[0] = "http://192.168.1.205/users/auth";  //аргументы для пост запроса
        args[1] = login.getText().toString();
        args[2] = pass.getText().toString();

        log.execute(args); // вызываем запрос

        err.setText(log.get().toString());

    }
}

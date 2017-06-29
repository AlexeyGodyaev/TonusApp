package com.caloriesdiary.caloriesdiary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ExecutionException;



public class AuthorizationActivity extends Activity {

    Button reg;
    Button logBtn;
    EditText login, pass;
    TextView err;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_layout);
        reg = (Button) findViewById(R.id.registrationButton);
        logBtn = (Button) findViewById(R.id.loginButton);
        login = (EditText) findViewById(R.id.editLogin);
        pass = (EditText) findViewById(R.id.editPassword);
        err = (TextView) findViewById(R.id.testRequestText);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();
        InitPreference();

    }

    public  void registrationClc(View view){
        Intent intent = new Intent(getApplicationContext(),RegistrationActivity.class);

        startActivity(intent);
    }

    public void loginClc(View view) throws InterruptedException, ExecutionException {
        Post log = new Post();

        String args[] = new String[3];

        args[0] = "http://192.168.1.205/users/auth";  //аргументы для пост запроса
        args[1] = login.getText().toString();
        args[2] = pass.getText().toString();

        log.execute(args); // вызываем запрос
        int status = -1;
        JSONObject JSans = log.get();
        try
        {
            err.setText("Status: " +JSans.getString("status"));
            status =  JSans.getInt("status");

            if(status == 1)
            {
                editor.putInt("PROFILE_ID",JSans.getInt("user_id"));
                editor.commit();
                Toast.makeText(getApplicationContext(), "Добро пожаловать, " + login.getText().toString() +" " + String.valueOf(sharedPref.getInt("PROFILE_ID",0)), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(status == 0)
            {
                err.setText("Неправильное имя пользователя или пароль");
            }
        }
        catch (Exception e)
        {
            err.setText(e.toString());
        }

    }
    public void guestClc(View view)
    {
        Intent intent = new Intent(getApplicationContext(),GuestMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void forgetpassClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ForgetPassActivity.class);
        startActivity(intent);
    }
    public void InitPreference()
    {

        if(sharedPref.getBoolean("IS_FIRST_LAUNCH",true))
        {
            Toast.makeText(getApplicationContext(),"Вы в приложении в первый раз ",Toast.LENGTH_LONG).show();
            editor.putBoolean("IS_FIRST_LAUNCH",false);
            editor.putInt("PROFILE_ID",0);
            editor.putBoolean("IS_PROFILE_CREATED",false);
        }


        editor.commit();
        Map<String, ?> allEntries = sharedPref.getAll();   //Увидеть все настройки
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}

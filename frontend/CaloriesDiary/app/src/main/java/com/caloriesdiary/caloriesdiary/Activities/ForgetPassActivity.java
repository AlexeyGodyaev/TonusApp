package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.R;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class ForgetPassActivity extends AppCompatActivity {
    EditText mail_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pass_layout);
        setTitle("Восстановление пароля");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        mail_edit = (EditText) findViewById(R.id.mail_edit);
    }
    public void sendClick(View view) throws InterruptedException, ExecutionException
    {
        Post log = new Post();

        String args[] = new String[2];

        args[0] = "http://caloriesdiary.ru/users/forgot_password";  //аргументы для пост запроса
        args[1] = mail_edit.getText().toString();

        log.execute(args); // вызываем запрос
        JSONObject JSans = log.get();

        try
        {
            if(JSans.getInt("status") == 1)
            {
                Toast.makeText(getApplicationContext(), "Новый пароль отправлен на почту " + mail_edit.getText() ,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,JSans.getString("msg"),Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Сервис не доступен" ,Toast.LENGTH_LONG).show();
        }
    }

    public void cancelClc(View view)
    {
        Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

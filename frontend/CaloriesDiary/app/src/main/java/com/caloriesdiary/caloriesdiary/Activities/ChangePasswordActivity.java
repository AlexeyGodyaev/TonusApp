package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Posts.Post;
import com.caloriesdiary.caloriesdiary.R;

import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText login,oldpass,newpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        setTitle("СМЕНА ПАРОЛЯ");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        login = (EditText) findViewById(R.id.changepass_login);
        oldpass = (EditText) findViewById(R.id.changepass_oldpassword);
        newpass = (EditText) findViewById(R.id.changepass_newpassword);
    }

    public void cancelClc(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onClick(View view)
    {
        try
        {
            if(newpass.getText().length() < 6)
            {
                Toast.makeText(this, "Длинна пароля не может быть меньше 6 символов ", Toast.LENGTH_LONG).show();
            }
            else {

                Post log = new Post();

                String args[] = new String[4];

                args[0] = "http://caloriesdiary.ru/users/change_password";  //аргументы для пост запроса
                args[1] = login.getText().toString();
                args[2] = oldpass.getText().toString();
                args[3] = newpass.getText().toString();

                log.execute(args); // вызываем запрос
                JSONObject JSans = log.get();

                if (JSans.getString("status").equals("1")) {

                    Toast.makeText(getApplicationContext(), "Пароль изменен", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

    }
}

package com.caloriesdiary.caloriesdiary;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.ExecutionException;

public class RegistrationActivity  extends AppCompatActivity {

    TextView error;
    EditText name, pass, passAgain, mail;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public boolean valid(String toExamine) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        setTitle("Регистрация");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        error = (TextView) findViewById(R.id.registrationErrorView);
        name = (EditText) findViewById(R.id.editRegLogin);
        pass =  (EditText) findViewById(R.id.editRegPassword);
        passAgain = (EditText) findViewById(R.id.editPasswordAgain);
        mail = (EditText) findViewById(R.id.editMail);
    }


    public void cancelRegClc(View view)
    {
        Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void regClc(View view)  {
        try {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher emailMatcher = pattern.matcher(mail.getText().toString());

            if (pass.getText().toString().equals(passAgain.getText().toString()) && pass.getText().toString().length() > 5) {

                if (emailMatcher.matches()) {

                    if (name.getText().toString().length() > 3 && valid(name.getText().toString())) {

                        String args[] = new String[4];
                        args[0] = "http://caloriesdiary.ru/users/register";
                        args[1] = name.getText().toString();
                        args[2] = pass.getText().toString();
                        args[3] = mail.getText().toString();

                        Post sendReg = new Post();
                        sendReg.execute(args);
                        String ans = sendReg.get().toString();

                        JSONObject js = sendReg.get();

                        js.getInt("status");
                        if (js.getInt("status") == 1) {
                            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            error.setText(js.getString("msg"));
                        }
                    } else {
                        error.setText("Логин должен содержать более 6 символов и должен состоять из букв латинского алфавита или цифр!");
                    }
                } else {
                    error.setText("Неверный адрес электронной почты");
                }
            } else {
                error.setText("Пароли не совпадают");
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

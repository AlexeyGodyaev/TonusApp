package com.caloriesdiary.caloriesdiary;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends Activity {

    TextView error;
    Button regBtn;
    EditText name, pass, passAgain, mail;

    Pattern pattern;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        error = (TextView) findViewById(R.id.registrationErrorView);
        regBtn = (Button) findViewById(R.id.registrationButton);
        name = (EditText) findViewById(R.id.editRegLogin);
        pass = (EditText) findViewById(R.id.editRegPassword);
        passAgain = (EditText) findViewById(R.id.editPasswordAgain);
        mail = (EditText) findViewById(R.id.editMail);

    }

    public void regClc(View view) throws  InterruptedException, ExecutionException{
<<<<<<< HEAD
        if(pass.getText().toString().equals(passAgain.getText().toString())){
        String args[] = new String[4];
            args[0]="http://94.130.12.179/users/register";
            args[1]=name.getText().toString();
            args[2]=pass.getText().toString();
            args[3]=mail.getText().toString();

            Post sendReg = new Post();
            sendReg.execute(args);
            String ans = sendReg.get().toString();

            JSONObject js = sendReg.get();
            try
            {
                js.getInt("status");
                error.setText(ans);
                if(js.getInt("status") == 1)
                {
                    Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),js.getString("msg"),Toast.LENGTH_LONG);
                }
            }
            catch (Exception e)
            {
=======
        try
        {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher emailMatcher = pattern.matcher(mail.getText().toString());
>>>>>>> Alex's-branch

            if(pass.getText().toString().equals(passAgain.getText().toString())){

                if(emailMatcher.matches()) {

                    if(name.getText().toString().length() > 3) {

                            String args[] = new String[4];
                            args[0] = "http://94.130.12.179/users/register";
                            args[1] = name.getText().toString();
                            args[2] = pass.getText().toString();
                            args[3] = mail.getText().toString();

                            Post sendReg = new Post();
                            sendReg.execute(args);
                            String ans = sendReg.get().toString();

                            JSONObject js = sendReg.get();

                            js.getInt("status");
                            error.setText(ans);
                            if (js.getInt("status") == 1) {
                                Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), js.getString("msg"), Toast.LENGTH_LONG);
                            }
                        }
                        else{
                            error.setText("Логин должен содержать более 3 символов");
                        }
                    }
                    else{
                        error.setText("Неверный адрес электронной почты");
                    }
            }
            else {
                error.setText("Пароли не совпадают");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}


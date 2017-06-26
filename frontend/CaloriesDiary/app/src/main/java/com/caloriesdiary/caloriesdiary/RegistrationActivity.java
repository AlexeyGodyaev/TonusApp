package com.caloriesdiary.caloriesdiary;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends Activity {

    TextView error;
    Button regBtn;
    EditText name, pass, passAgain, mail;

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
        if(pass.getText().toString().equals(passAgain.getText().toString())){
        String args[] = new String[4];
            args[0]="http://192.168.1.205/users/register";
            args[1]=name.getText().toString();
            args[2]=pass.getText().toString();
            args[3]=mail.getText().toString();

            Post sendReg = new Post();
            sendReg.execute(args);

            error.setText(sendReg.get().toString());
        }
        else error.setText("Введенные вами пароли должны быть эквивалентны");

    }
}


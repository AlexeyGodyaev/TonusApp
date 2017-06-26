package com.caloriesdiary.caloriesdiary;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        name = (EditText) findViewById(R.id.editLogin);
        pass = (EditText) findViewById(R.id.editPassword);
        passAgain = (EditText) findViewById(R.id.editPasswordAgain);
        mail = (EditText) findViewById(R.id.editMail);
    }

    public void regClc(View view){
        if(pass.getText().toString()==passAgain.getText().toString()){

        }
        else error.setText("Введенные вами пароли должны быть эдентичны");

    }
}


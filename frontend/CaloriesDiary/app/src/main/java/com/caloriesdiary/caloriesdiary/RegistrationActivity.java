package com.caloriesdiary.caloriesdiary;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends Activity {

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

        error = findViewById(R.id.registrationErrorView);
        //regBtn = (Button) findViewById(R.id.registrationButton);
        name =  findViewById(R.id.editRegLogin);
        pass =  findViewById(R.id.editRegPassword);
        passAgain =  findViewById(R.id.editPasswordAgain);
        mail =  findViewById(R.id.editMail);
    }

    public String hashPass(String pass) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashData = digest.digest(pass.getBytes("UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashData.length; i++) {
            sb.append(Integer.toString((hashData[i] & 0xff) + 0x100, 16).substring(1));
        }

        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();

        return sb.toString();
    }

    public void regClc(View view) throws InterruptedException, ExecutionException {
        try {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher emailMatcher = pattern.matcher(mail.getText().toString());

            if (pass.getText().toString().equals(passAgain.getText().toString()) && pass.getText().toString().length() > 5) {

                if (emailMatcher.matches()) {

                    if (name.getText().toString().length() > 3 && valid(name.getText().toString())) {

                        String args[] = new String[4];
                        args[0] = "http://94.130.12.179/users/register";
                        args[1] = name.getText().toString();
                        args[2] = hashPass(pass.getText().toString());
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

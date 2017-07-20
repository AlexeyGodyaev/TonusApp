package com.caloriesdiary.caloriesdiary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;


public class AuthorizationActivity extends Activity {

    GoogleApiClient m;
    GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    String tkn;

    Post log;

    Button reg,logBtn;
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        m = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        try {
            JSONArray jsonArray;
            JSONObject jsn;
            File f = new File(getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                jsonArray = jsn.getJSONArray("today_params");
                jsn.remove("today_params");

                Calendar c = Calendar.getInstance();

                if(jsonArray.length()>0 && !jsonArray.getJSONObject(jsonArray.length()-1).getString("date")
                        .equals( String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(c.get(Calendar.MONTH)) +
                                "." + String.valueOf(c.get(Calendar.YEAR)))){
                    File food = new File(getCacheDir(), "Food.txt");
                    if(food.exists())
                    food.delete();

                    File active = new File(getCacheDir(), "Actions.txt");
                    if(active.exists())
                    active.delete();
                }
            }
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void googleClc(View view)
    {
        switch (view.getId()) {
            case R.id.googleAccountTextView:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(m);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    public void handleSignInResult(GoogleSignInResult result) throws InterruptedException, ExecutionException {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            Post log = new Post();

            String args[] = new String[5];

            args[0] = "http://94.130.12.179/users/google_auth";  //аргументы для пост запроса
            args[1] = acct.getId();
            args[2] = acct.getEmail();
            args[3] = acct.getGivenName();
            args[4] = FirebaseInstanceId.getInstance().getToken();

            log.execute(args); // вызываем запрос
            int status = -1;
            JSONObject JSans = log.get();

            try {
                err.setText(JSans.getString("status"));
                status = JSans.getInt("status");
                if (status == 1) {
                    editor.putInt("PROFILE_ID", JSans.getInt("user_id"));
                    editor.putString("userName", JSans.getString("username"));
                    editor.putString("userMail", JSans.getString("email"));
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Добро пожаловать, " + acct.getGivenName() + " " + String.valueOf(sharedPref.getInt("PROFILE_ID", 0)), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            catch(Exception e)
            {
                Toast.makeText(this,  e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, result.getStatus().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Результат авторизации возращается из GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                handleSignInResult(result);
            }
            catch (Exception e)
            {

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        m.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m.disconnect();
    }

    public  void registrationClc(View view){
        Intent intent = new Intent(getApplicationContext(),RegistrationActivity.class);

        startActivity(intent);
    }

    public void loginClc(View view) throws InterruptedException, ExecutionException {
        try {
        Post log = new Post();

        String args[] = new String[4];

        args[0] = "http://94.130.12.179/users/auth";  //аргументы для пост запроса
        args[1] = login.getText().toString();
        args[2] = pass.getText().toString();
        args[3] = FirebaseInstanceId.getInstance().getToken();

        log.execute(args); // вызываем запрос
        int status = -1;
        JSONObject JSans = log.get();

            err.setText("Status: " + JSans.getString("status"));
            status = JSans.getInt("status");
            if (status == 1) {
                editor.putInt("PROFILE_ID", JSans.getInt("user_id"));
                editor.putString("userName", JSans.getString("username"));
                editor.putString("userMail", JSans.getString("email"));
                editor.commit();
                Toast.makeText(getApplicationContext(), "Добро пожаловать, " + login.getText().toString() + " " + String.valueOf(sharedPref.getInt("PROFILE_ID", 0)), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //Запуск службы ControlService (Каждый час)
                 Intent startServiceIntent = new Intent(this,
                        ControlService.class);
                PendingIntent startWebServicePendingIntent = PendingIntent.getService(this, 0,
                        startServiceIntent, 0);

                AlarmManager alarmManager = (AlarmManager) this
                        .getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(), 30*1000*60,
                        startWebServicePendingIntent);

            } else if (status == 0) {
                err.setText("Неправильное имя пользователя или пароль");
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Сервис не доступен " + e.toString(), Toast.LENGTH_LONG ).show();
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

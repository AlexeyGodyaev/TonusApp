package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class AccSettingActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private ListView settingList;
    private String flag = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_settings_layout);

        setTitle("Аккаунт");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            File f = new File(getCacheDir(), "Auth");
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inObject = new ObjectInputStream(in);
            JSONObject js = new JSONObject(inObject.readObject().toString());
            flag = js.getString("flag");
            inObject.close();
        } catch (Exception e){

        }

        settingList = (ListView) findViewById(R.id.acc_setting_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,initList());

        settingList.setAdapter(adapter);

        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);

        setListener();
    }

    private void setListener(){
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case 0:
                        onChangePass();
                        break;
                    case 1:
                        onExitClc();
                        break;
                    case 2:
                        onDeleteClc();
                        break;
                }
            }
        });
    }

    private List<String> initList(){
        List<String> list = new ArrayList<>();

        if(flag.equals("user"))
            list.add("Смена пароля");

        if(flag.equals("guest"))
            list.add("Регистрация");

        list.add("Выйти из аккаунта");
        //if(!flag.equals("guest"))
        list.add("Удаление аккаунта");


        return list;
    }

    public void onDeleteClc(){
        try {
            final View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_layout, null);

            final Button cancelBtn = content.findViewById(R.id.cancel_delete_button);
            final TextView OKBtn = content.findViewById(R.id.accept_delete_acc);

            final EditText pass = content.findViewById(R.id.delete_dialog_pass);

            final AlertDialog.Builder builder = new AlertDialog.Builder(AccSettingActivity.this);

            builder.setView(content);
            final AlertDialog alert = builder.create();
            alert.show();

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.cancel();
                }
            });

            OKBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Post log = new Post();

                        String args[] = new String[3];

                        args[0] = "http://caloriesdiary.ru/users/delete";  //аргументы для пост запроса
                        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                        args[2] = pass.getText().toString();


                        log.execute(args); // вызываем запрос
                        JSONObject JSans = log.get();
                        if (JSans.getString("status").equals("1")) {
                            Toast.makeText(getApplicationContext(), "Учетная запись удалена", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        alert.dismiss();
                        //Toast.makeText(getApplicationContext(), jObject.toString() , Toast.LENGTH_LONG).show();
                    } catch (Exception iEx) {
                        Toast.makeText(getApplicationContext(), iEx.toString(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onChangePass(){
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void clearAllFiles(){
        File stat = new File(getCacheDir(), "Today_params.txt");
        stat.delete();
        File active = new File(getCacheDir(), "Actions.txt");
        active.delete();
        File food = new File(getCacheDir(), "Food.txt");
        food.delete();
    }

    public void onExitClc(){

        clearAllFiles();
        Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);
        File f = new File(getCacheDir(), "Auth");
        f.delete();

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;


public class DeleteAccountActivity extends AppCompatActivity {
    EditText pass;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_layout);
        pass = (EditText) findViewById(R.id.confirm_pass);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
    }

    public void onClickDelete(View view)
    {
        try
        {
            Post log = new Post();

            String args[] = new String[3];

            args[0] = "http://caloriesdiary.ru/users/delete";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[2] = pass.getText().toString();


            log.execute(args); // вызываем запрос
            JSONObject JSans = log.get();
            if(JSans.getString("status").equals("1"))
            {
                Toast.makeText(getApplicationContext(),"Учетная запись удалена",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),AuthorizationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

    }
}

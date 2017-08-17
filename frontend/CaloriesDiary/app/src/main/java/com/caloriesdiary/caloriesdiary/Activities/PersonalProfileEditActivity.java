package com.caloriesdiary.caloriesdiary.Activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.HTTP.GetImage;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.HTTP.PostAvatar;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.R;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;


public class PersonalProfileEditActivity extends AppCompatActivity implements CallBackListener{
    Spinner active_spin, gender_spin;
    TextView name,age,height,weight,sleep,awake;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String awakestr;
    private Toolbar mToolbar;
    Post log;
    Boolean initprofile = false;

    CollapsingToolbarLayout mCollapsingToolbarLayout;
    ImageView imageView;
    private final int reqcode = 1;
    int DIALOG_TIME = 1;
    int myHour = 14;
    int myMinute = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_edit_layout);

        active_spin = (Spinner) findViewById(R.id.activity_spinner);
        gender_spin = (Spinner) findViewById(R.id.edit_gender_spinner);
        name = (EditText) findViewById(R.id.name_edit);
        age = (EditText) findViewById(R.id.age_edit);
        height = (EditText) findViewById(R.id.height_edit);
        weight = (EditText) findViewById(R.id.weight_edit);
        sleep = (EditText) findViewById(R.id.sleep_edit);
        awake = (EditText) findViewById(R.id.edit_awake_time);


        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_edit);
        imageView = mCollapsingToolbarLayout.findViewById(R.id.backdrop_edit);
        setSupportActionBar(mToolbar);


        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        initProfile();
        setAvatar();
       // InitPreference();
    }

    private void initProfile(){
        try
        {
            log = new Post();
            log.setListener(this);
            String args[] = new String[3];

            args[0] = "http://caloriesdiary.ru/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[2] = FirebaseInstanceId.getInstance().getToken();
            initprofile = true;
            log.execute(args); // вызываем запрос

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }


    private void setAvatar(){

        mCollapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickphoto = new Intent(Intent.ACTION_PICK);
                pickphoto.setType("image/*");
                startActivityForResult(pickphoto,reqcode);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case reqcode:
                if(resultCode == RESULT_OK){
                    try {


                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Drawable dr = Drawable.createFromStream(imageStream,"avatar.png");
                        SavePictureToServer(dr);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }}
    private void SavePictureToServer(Drawable dr) {


        try {
            ImagePost imagepost = new ImagePost();
            imagepost.execute(dr);
            JSONObject json = imagepost.get();

        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    public class ImagePost extends AsyncTask<Drawable,Void,JSONObject>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Drawable... dr) {

            PostAvatar postAvatar = new PostAvatar();
            JSONObject json = postAvatar.PostAvatar(String.valueOf(sharedPref.getInt("PROFILE_ID", 0)),FirebaseInstanceId.getInstance().getToken(),((BitmapDrawable) dr[0]).getBitmap());
            return json;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(TextView text) {
        return (text.getText().toString().equals("") || text.getText().toString().length()==0);
    }

    public void onClick(View view)  {

        try {
            String selected_activity = String.valueOf(active_spin.getSelectedItemPosition() + 1);
            String selected_gender;

            if (!isEmpty(name) && !isEmpty(weight) && !isEmpty(age) && !isEmpty(height)) {

                if (Integer.parseInt(weight.getText().toString()) > 10
                        && Integer.parseInt(weight.getText().toString()) < 750
                        && Integer.parseInt(age.getText().toString()) < 130
                        && Integer.parseInt(height.getText().toString()) < 300
                        && Integer.parseInt(sleep.getText().toString()) < 24) {

                    if (gender_spin.getSelectedItemPosition()==0) {
                        selected_gender = "1";
                    }  else {
                        selected_gender = "2";
                    }

                    Post log = new Post();
                    log.setListener(this);
                    String args[] = new String[11];

                    args[0] = "http://caloriesdiary.ru/users/save_user_chars";  //аргументы для пост запроса
                    args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                    args[2] = name.getText().toString();
                    args[3] = weight.getText().toString();
                    args[4] = height.getText().toString();
                    args[5] = selected_gender;
                    args[6] = age.getText().toString();
                    args[7] = selected_activity;
                    args[8] = sleep.getText().toString();
                    args[9] = awakestr;
                    args[10] = FirebaseInstanceId.getInstance().getToken();

                    log.execute(args); // вызываем запрос

                    Toast.makeText(getApplicationContext(), log.get().toString(), Toast.LENGTH_LONG).show();
                    editor = sharedPref.edit();
                    editor.putBoolean("IS_PROFILE_CREATED", true);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Некоторые поля введены некорректно", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onAwakeTimeClick(View view)
    {
        showDialog(DIALOG_TIME);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            return  new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;

            if (myMinute <10) {
                awake.setText(String.valueOf(myHour) + ":0" + String.valueOf(myMinute));
                awakestr = String.valueOf(myHour) + ":0" + String.valueOf(myMinute) + ":00";
            }
            else {
                awake.setText(String.valueOf(myHour) + ":" + String.valueOf(myMinute));
                awakestr = String.valueOf(myHour) + ":" + String.valueOf(myMinute) + ":00";
            }
        }
    };

    public void InitPreference()
    {

        Map<String, ?> allEntries = sharedPref.getAll();   //Увидеть все настройки
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    @Override
    public void callback() {
        if(initprofile) {
            try {
                JSONObject JSans = log.get();

                // Toast.makeText(getApplicationContext(),String.valueOf(sharedPref.getInt("PROFILE_ID",0)) + " " +JSans.toString(),Toast.LENGTH_LONG).show();

                //Toast.makeText(getApplicationContext(),JSans.toString(),Toast.LENGTH_LONG).show();

                if (JSans.getString("status").equals("1")) {
                    name.setText(JSans.getJSONObject("userChars").getString("realName"));
                    age.setText(JSans.getJSONObject("userChars").getString("age"));
                    height.setText(JSans.getJSONObject("userChars").getString("height"));
                    weight.setText(JSans.getJSONObject("userChars").getString("weight"));
                    if (JSans.getJSONObject("userChars").getString("sex").equals("1")) {
                        gender_spin.setSelection(0);
                    } else {
                        gender_spin.setSelection(1);
                    }
                    switch (JSans.getJSONObject("userChars").getString("activityType")) {
                        case "1":
                            active_spin.setSelection(0);
                            break;
                        case "2":
                            active_spin.setSelection(1);
                            break;
                        case "3":
                            active_spin.setSelection(2);
                            break;
                        case "4":
                            active_spin.setSelection(3);
                            break;
                        case "5":
                            active_spin.setSelection(4);
                            break;
                    }
                    sleep.setText(JSans.getJSONObject("userChars").getString("avgdream"));
                    awake.setText(JSans.getJSONObject("userChars").getString("wokeup").substring(0, 5));
                    awakestr = JSans.getJSONObject("userChars").getString("wokeup");

                    String avatar = JSans.getJSONObject("userChars").getString("avatar");
                    GetImage getImage = new GetImage();
                    getImage.execute(avatar);
                    Drawable dr = getImage.get();
                    imageView.setImageDrawable(dr);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

    }
}

package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class PersonalProfileActivity extends AppCompatActivity {

    ImageView photo_view;
    TextView name_text,age_text,weight_text,height_text,gender_text;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;
    private final int reqcode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        editor = sharedPref.edit();
        photo_view = (ImageView) findViewById(R.id.personal_photo);
        name_text = (TextView) findViewById(R.id.name_text);
        age_text = (TextView) findViewById(R.id.age_text);
        weight_text = (TextView) findViewById(R.id.weight_text);
        height_text = (TextView) findViewById(R.id.height_text);
        gender_text = (TextView) findViewById(R.id.gender_text);

        try
        {
            Post log = new Post();

            String args[] = new String[2];

            args[0] = "http://94.130.12.179/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));


            log.execute(args); // вызываем запрос
            JSONObject JSans = log.get();

           // Toast.makeText(getApplicationContext(),String.valueOf(sharedPref.getInt("PROFILE_ID",0)) + " " +JSans.toString(),Toast.LENGTH_LONG).show();

            Toast.makeText(getApplicationContext(),JSans.getJSONArray("userChars").getJSONObject(0).getString("realName"),Toast.LENGTH_LONG).show();
                name_text.setText("Имя: " + JSans.getJSONArray("userChars").getJSONObject(0).getString("realName"));
            if(JSans.getJSONArray("userChars").getJSONObject(0).getString("sex").equals("1"))
            {
                gender_text.setText("Пол: мужской");
            }
            else
            {
                gender_text.setText("Пол: женский");
            }
                age_text.setText("Возраст: " + JSans.getJSONArray("userChars").getJSONObject(0).getString("age"));
                weight_text.setText("Вес: " + JSans.getJSONArray("userChars").getJSONObject(0).getString("weight"));
                height_text.setText("Рост: " + JSans.getJSONArray("userChars").getJSONObject(0).getString("height"));

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
        File imgFile = new  File("/data/user/0/com.caloriesdiary.caloriesdiary/cache/avatar.jpg");

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.personal_photo);

            myImage.setImageBitmap(myBitmap);

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

    public void onClickEditPhoto(View view)
    {
        Toast.makeText(getApplicationContext(),this.getCacheDir().toString(),Toast.LENGTH_LONG).show();
        Intent pickphoto = new Intent(Intent.ACTION_PICK);
        pickphoto.setType("image/*");
        startActivityForResult(pickphoto,reqcode);
    }
    public void onClickEditProfile(View view)
    {
        Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
    public void onClickDeleteProfile(View view)
    {
            Intent intent = new Intent(getApplicationContext(),DeleteAccountActivity.class);
            startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case reqcode:
                if(resultCode == RESULT_OK){
                    try {

                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        photo_view.setImageBitmap(selectedImage);
                        SavePicture(photo_view,this.getCacheDir().toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }}

    private String SavePicture(ImageView iv, String folderToSave)
    {
        OutputStream fOut = null;
        Time time = new Time();
        time.setToNow();

        try {
            File file = new File(folderToSave,"avatar.jpg"); // создать уникальное имя для файла основываясь на дате сохранения
            fOut = new FileOutputStream(file);
            iv.buildDrawingCache();
            Bitmap bitmap = iv.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName()); // регистрация в фотоальбоме
            Toast.makeText(getApplicationContext(),"Сохарненик",Toast.LENGTH_LONG).show();
        }
        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
        {
            return e.getMessage();
        }
        return "";
    }
}

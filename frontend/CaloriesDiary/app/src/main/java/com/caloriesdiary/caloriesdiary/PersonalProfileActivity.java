package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class PersonalProfileActivity extends AppCompatActivity {

    ProfileAntropometryFragment fragment;
    FragmentManager manager;
    FragmentTransaction transaction;
    boolean antropometryFlag = true;
    TextView name_text,age_text,weight_text,height_text,gender_text, wakeup_text, sleep_text;
    SharedPreferences sharedPref = null;
    private Toolbar mToolbar;
    private final int reqcode = 1;
    JSONObject JSans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_layout);
        manager = getSupportFragmentManager();
        fragment = new ProfileAntropometryFragment();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        name_text = (TextView) findViewById(R.id.name_text);
        age_text = (TextView) findViewById(R.id.age_text);
        sleep_text = (TextView) findViewById(R.id.sleep_time_text);
        wakeup_text = (TextView) findViewById(R.id.wakeup_time_text);
        weight_text = (TextView) findViewById(R.id.weight_text);
        height_text = (TextView) findViewById(R.id.height_text);
        gender_text = (TextView) findViewById(R.id.gender_text);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Клие на актионебар",Snackbar.LENGTH_LONG).show();
               // Toast.makeText(getApplicationContext(),this.getCacheDir().toString(),Toast.LENGTH_LONG).show();
                Intent pickphoto = new Intent(Intent.ACTION_PICK);
                pickphoto.setType("image/*");
                startActivityForResult(pickphoto,reqcode);
            }
        });

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
      //  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try
        {
            Post log = new Post();

            String args[] = new String[2];

            args[0] = "http://caloriesdiary.ru/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));


            log.execute(args); // вызываем запрос
            JSans = log.get();

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
            wakeup_text.setText("Время пробуждения: "+JSans.getJSONArray("userChars").getJSONObject(0).getString("wokeup").substring(0,5));
            sleep_text.setText("Среднее время сна: "+JSans.getJSONArray("userChars").getJSONObject(0).getString("avgdream"));

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
        File imgFile = new  File("/data/user/0/com.caloriesdiary.caloriesdiary/cache/avatar.png");
        //String imgPath = "/data/user/0/com.caloriesdiary.caloriesdiary/cache/avatar.png";

        if(imgFile.exists()){

            try
            {
                final Uri imageUri = Uri.fromFile(imgFile);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Drawable dr = Drawable.createFromStream(imageStream,"avatar.png");

                //dr.setAlpha(30);
                ImageView imageView = mToolbar.findViewById(R.id.toolbar_image);


                imageView.setImageDrawable(dr);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                //Есть 2 стула, один имэйджью обрезанный, с другой бэкграунд растянутый. Какой юзнешь, а какой с проекта пошлешь?



               // mToolbar.setBackground(dr);
            }
            catch(Exception e)
            {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }


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
        Intent intent = new Intent(getApplicationContext(),SetAGoal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            intent.putExtra("realName",JSans.getJSONArray("userChars").getJSONObject(0).getString("realName"));
            intent.putExtra("sex",JSans.getJSONArray("userChars").getJSONObject(0).getString("sex"));
            intent.putExtra("age",JSans.getJSONArray("userChars").getJSONObject(0).getString("age"));
            intent.putExtra("height",JSans.getJSONArray("userChars").getJSONObject(0).getString("height"));
        }catch (Exception e){

        }

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
                        Drawable dr = Drawable.createFromStream(imageStream,"avatar.png");
                        mToolbar.setBackground(dr);
                       //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        //photo_view.setImageBitmap(selectedImage);
                        SavePicture(this.getCacheDir().toString(),imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }}

    private String SavePicture(String folderToSave, Uri uri)
    {

        try {
            File file = new File(folderToSave,"avatar.png"); // создать уникальное имя для файла

            final int chunkSize = 1024;  // We'll read in one kB at a time
            byte[] imageData = new byte[chunkSize];

            try {
                InputStream in = getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(file);  // I'm assuming you already have the File object for where you're writing to

                int bytesRead;
                while ((bytesRead = in.read(imageData)) > 0) {
                    out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
                }
                in.close();
                out.close();
            } catch (Exception ex) {

            }


            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName()); // регистрация в фотоальбоме
            Toast.makeText(getApplicationContext(),"Сохарненик",Toast.LENGTH_LONG).show();
        }
        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
        {
            return e.getMessage();
        }
        return "";
    }

    public void profileAntrClc(View view) {
        transaction = manager.beginTransaction();
        try {
            if (antropometryFlag) {
                transaction.add(R.id.profile_antr_container, fragment);
                antropometryFlag = false;

            } else {
                transaction.remove(fragment);
                antropometryFlag = true;
            }

            transaction.commit();

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}

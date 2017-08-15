package com.caloriesdiary.caloriesdiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.HTTP.GetImage;
import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.Fragments.ProfileAntropometryFragment;
import com.caloriesdiary.caloriesdiary.HTTP.PostAvatar;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;



public class PersonalProfileActivity extends AppCompatActivity implements CallBackListener {

    ProfileAntropometryFragment fragment;
    FragmentManager manager;
    FragmentTransaction transaction;
    boolean antropometryFlag = true;
    TextView life_style,age_text,weight_text,height_text,gender_text, wakeup_text, sleep_text;
    SharedPreferences sharedPref = null;
    private Toolbar mToolbar;
    private final int reqcode = 1;
    JSONObject JSans;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_layout);
        manager = getSupportFragmentManager();
        fragment = new ProfileAntropometryFragment();
        sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
        life_style = (TextView) findViewById(R.id.life_style_text);
        age_text = (TextView) findViewById(R.id.age_text);
        sleep_text = (TextView) findViewById(R.id.sleep_time_text);
        wakeup_text = (TextView) findViewById(R.id.wakeup_time_text);
        weight_text = (TextView) findViewById(R.id.weight_text);
        height_text = (TextView) findViewById(R.id.height_text);
        gender_text = (TextView) findViewById(R.id.gender_text);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        imageView = mCollapsingToolbarLayout.findViewById(R.id.backdrop);
//
//
//        mToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view,"Клие на актионебар",Snackbar.LENGTH_LONG).show();
//               // Toast.makeText(getApplicationContext(),this.getCacheDir().toString(),Toast.LENGTH_LONG).show();
//                Intent pickphoto = new Intent(Intent.ACTION_PICK);
//                pickphoto.setType("image/*");
//                startActivityForResult(pickphoto,reqcode);
//            }
//        });

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initProfile();
        setAvatar();
    }


    private void initProfile() {
        try {
            Post log = new Post();
            log.setListener(this);
            String args[] = new String[3];

            args[0] = "http://caloriesdiary.ru/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
            args[2] = FirebaseInstanceId.getInstance().getToken();

            log.execute(args); // вызываем запрос
            JSans = log.get();

            Toast.makeText(getApplicationContext(),String.valueOf(sharedPref.getInt("PROFILE_ID",0)) + " " +JSans.toString(),Toast.LENGTH_LONG).show();

            Toast.makeText(getApplicationContext(), JSans.getJSONObject("userChars").getString("realName"), Toast.LENGTH_LONG).show();
            setTitle(JSans.getJSONObject("userChars").getString("realName"));

            life_style.setText(JSans.getJSONObject("userChars").getString("activityType"));
            //String te[] = (String[]) findViewById( R.array.activitylist);

            if (JSans.getJSONObject("userChars").getString("sex").equals("1")) {
                gender_text.setText("Мужской");
            } else {
                gender_text.setText("Женский");
            }
            age_text.setText(JSans.getJSONObject("userChars").getString("age"));
            weight_text.setText(JSans.getJSONObject("userChars").getString("weight"));
            height_text.setText(JSans.getJSONObject("userChars").getString("height"));
            wakeup_text.setText(JSans.getJSONObject("userChars").getString("wokeup").substring(0, 5));
            sleep_text.setText(JSans.getJSONObject("userChars").getString("avgdream"));

            String avatar = JSans.getJSONObject("userChars").getString("avatar");
            GetImage getImage = new GetImage();
            getImage.execute(avatar);
            Drawable dr = getImage.get();
            imageView.setImageDrawable(dr);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            Toast.makeText(this, avatar, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }
    public Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            return null;
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


        File imgFile = new  File("/data/user/0/com.caloriesdiary.caloriesdiary/cache/avatar.png");

        if(imgFile.exists()){

            try
            {
                final Uri imageUri = Uri.fromFile(imgFile);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Drawable dr = Drawable.createFromStream(imageStream,"avatar.png");

                //dr.setAlpha(30);



                //imageView.setImageDrawable(dr);
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

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


                       // imageView.setImageDrawable(dr);


                        //mToolbar.setBackground(dr);
                       //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        //photo_view.setImageBitmap(selectedImage);
                        SavePictureToServer(dr);
                        //SavePicture(this.getCacheDir().toString(),imageUri);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }}

    private void SavePictureToServer(Drawable dr) {


        try {

//            PostAvatar postAvatar = new PostAvatar();
//            JSONObject json = postAvatar.PostAvatar(String.valueOf(sharedPref.getInt("PROFILE_ID", 0)),FirebaseInstanceId.getInstance().getToken(),((BitmapDrawable) dr).getBitmap());
           // Toast.makeText(this, "Что получилось: " + json.toString(), Toast.LENGTH_LONG).show();

            ImagePost imagepost = new ImagePost();
            imagepost.execute(dr);
            JSONObject json = imagepost.get();
            Toast.makeText(this, "Что получилось: " + json.toString(), Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

//       Post post = new Post();
//        post.setListener(this);
//        String args[] = new String[4];
//        args[0] = "http://caloriesdiary.ru/users/set_avatar";
//        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
//        args[2] = FirebaseInstanceId.getInstance().getToken();
//        Bitmap bm = ((BitmapDrawable) dr).getBitmap();
//        ByteArrayOutputStream bao = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//       // byte [] ba = bao.toByteArray();
//
//        args[3] = Base64.encodeToString(bao.toByteArray(),Base64.DEFAULT);
////
////        byte[] bytes = Base64.decode(args[3], Base64.DEFAULT);
////
////        Bitmap bm2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
////        Drawable dr2 = new BitmapDrawable(bm2);
////        imageView.setImageDrawable(dr2);
//        post.execute(args);
//        try {
//          //  setTitle(post.get().toString());
//            Log.e("POSTIMAGE",post.get().toString());
//            Toast.makeText(this, post.get().toString(), Toast.LENGTH_LONG).show();
//
//        }
//        catch (Exception e)
//        {
//
//        }

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

    @Override
    public void callback() {

    }
}

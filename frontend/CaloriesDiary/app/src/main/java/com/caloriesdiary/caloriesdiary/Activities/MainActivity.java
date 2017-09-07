package com.caloriesdiary.caloriesdiary.Activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Fragments.MainActivityFragment;
import com.caloriesdiary.caloriesdiary.Fragments.MainDiaryFragment;
import com.caloriesdiary.caloriesdiary.Fragments.MainFoodFragment;
import com.caloriesdiary.caloriesdiary.Fragments.MainStatFragment;
import com.caloriesdiary.caloriesdiary.Fragments.MainTodayFragment;

import com.caloriesdiary.caloriesdiary.HTTP.GetDays;

import com.caloriesdiary.caloriesdiary.HTTP.GetImage;

import com.caloriesdiary.caloriesdiary.HTTP.Post;
import com.caloriesdiary.caloriesdiary.Items.CallBackListener;
import com.caloriesdiary.caloriesdiary.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , CallBackListener {

    SharedPreferences sharedPref;
    TextView userName, userMail, currentTime;
    ImageView userAvatar;
    boolean setavatar = false;
    Post postav;
    Calendar calendar;
    private FragmentManager manager;
    MainTodayFragment todayfragment;
    MainDiaryFragment diaryfragment;
    MainStatFragment statfragment;
    MainFoodFragment foodfragment;
    MainActivityFragment activityfragment;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
            currentTime = (TextView) findViewById(R.id.main_time_textview);
            calendar = Calendar.getInstance();
            currentTime.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + getMonth(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR));
            sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
            manager = getSupportFragmentManager();
            todayfragment = new MainTodayFragment();
            diaryfragment = new MainDiaryFragment();
            statfragment = new MainStatFragment();
            foodfragment = new MainFoodFragment();
            activityfragment = new MainActivityFragment();
            fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(R.id.main_todayCont, todayfragment);
            fragmentTransaction.add(R.id.main_diaryCont, diaryfragment);
            fragmentTransaction.add(R.id.main_statCont, statfragment);
            fragmentTransaction.add(R.id.main_foodCont, foodfragment);
            fragmentTransaction.add(R.id.main_activityCont, activityfragment);
            fragmentTransaction.commit();

//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            checkUserParams();


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View v = navigationView.getHeaderView(0);
            userMail = v.findViewById(R.id.head_usermail_text);
            userName = v.findViewById(R.id.head_username_text);
            userAvatar = v.findViewById(R.id.head_useravatar);

            userMail.setText(sharedPref.getString("userMail", "Нет данных"));
            userName.setText(sharedPref.getString("userName", "Нет данных"));
            getArchive();
    }

    private void getArchive() {
        try {
            File f = new File(getCacheDir(), "Today_params.txt");
            if(!f.exists()) {
                JSONObject jsn;
                GetDays get = new GetDays();

                String args[] = new String[2];
                args[0] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
                args[1] = FirebaseInstanceId.getInstance().getToken();


                get.execute(args);
                jsn = new JSONObject(get.get());
                if (jsn.getString("status").equals("1")) {

                    FileOutputStream out = new FileOutputStream(f);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(jsn.toString());
                    outObject.flush();
                    out.getFD().sync();
                    outObject.close();
                }

                JSONArray days = jsn.getJSONArray("days");

                Calendar calendar = Calendar.getInstance();
                String date = String.valueOf(calendar.get(Calendar.YEAR));
                if (calendar.get(Calendar.MONTH) < 9)
                    date += "-0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
                else date += "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
                if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
                    date += "-0" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                else date += "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                if (days.getJSONObject(days.length()-1).getString("date").equals(date)){

                    File active = new File(getCacheDir(), "Actions.txt");
                    JSONObject jObject = new JSONObject();
                    JSONArray jArr = new JSONArray(days.getJSONObject(days.length()-1).get("activities").toString());
                    jObject.put("active", jArr);

                    FileOutputStream out = new FileOutputStream(active);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(jObject.toString());
                    outObject.flush();
                    out.getFD().sync();
                    outObject.close();

                    File food = new File(getCacheDir(), "Food.txt");
                    jObject = new JSONObject();
                    jArr = new JSONArray(days.getJSONObject(days.length()-1).get("food").toString());
                    jObject.put("food", jArr);

                    FileOutputStream outFood = new FileOutputStream(food);
                    ObjectOutputStream outFoodObject = new ObjectOutputStream(outFood);
                    outFoodObject.writeObject(jObject.toString());
                    outFoodObject.flush();
                    outFood.getFD().sync();
                    outFoodObject.close();
                }
                checkUserParams();
            }
        } catch (Exception e) {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void checkUserParams(){
        File f = new File(getCacheDir(), "Today_params.txt");
        if(f.exists()) {
            try {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();

                JSONObject jsn = new JSONObject(text);
                JSONArray days = jsn.getJSONArray("days");

                if (days != null) {
                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < days.length(); i++) {
                        list.add(days.getJSONObject(i));
                    }
                    Calendar data = Calendar.getInstance();
                    String month, day, year;
                    year = list.get(0).getString("date").substring(0,4);
                    month = list.get(0).getString("date").substring(5,7);
                    day = list.get(0).getString("date").substring(8,10);
                    data.set(Integer.valueOf(year),Integer.valueOf(month)-1,Integer.valueOf(day));

//                    days = new JSONArray();
                    //for (int i = 1; i < list.size(); i++) {

                    for (int i=0; i<list.size()-1; i++) {
                       // Toast.makeText(this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
                        String dat;

                        data.set(Calendar.DAY_OF_YEAR, data.get(Calendar.DAY_OF_YEAR) + 1);
                        dat = String.valueOf(data.get(Calendar.YEAR)) + "-";
                        if (data.get(Calendar.MONTH) < 9)
                            dat += "0" + String.valueOf(data.get(Calendar.MONTH) + 1) + "-";
                        else dat += String.valueOf(data.get(Calendar.MONTH) + 1) + "-";
                        if (data.get(Calendar.DAY_OF_MONTH) < 10)
                            dat += "0" + String.valueOf(data.get(Calendar.DAY_OF_MONTH));
                        else dat += String.valueOf(data.get(Calendar.DAY_OF_MONTH));

                        if (!list.get(i + 1).getString("date").equals(dat) && i < list.size()-1) {
                            jsn = new JSONObject(list.get(i).toString());
                            jsn.put("date", dat);
                            jsn.put("note","Данные скопированы из предыдущего дня");
                           // days.put(list.get(i));
                            list.add(i + 1, jsn);
                            //
                            // Toast.makeText(this, list.get(i).getString("date"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    for(int i=1; i<list.size(); i++){
                            if (list.get(i).getString("mass").equals("0"))
                                if (!list.get(i - 1).getString("mass").equals("0"))
                                    list.get(i).put("mass", list.get(i - 1).getString("mass"));

                            if (list.get(i).getString("shoulders").equals("0"))
                                if (!list.get(i - 1).getString("shoulders").equals("0"))
                                    list.get(i).put("shoulders", list.get(i - 1).getString("shoulders"));

                            if (list.get(i).getString("rHand").equals("0"))
                                if (!list.get(i - 1).getString("rHand").equals("0"))
                                    list.get(i).put("rHand", list.get(i - 1).getString("rHand"));

                            if (list.get(i).getString("lHand").equals("0"))
                                if (!list.get(i - 1).getString("lHand").equals("0"))
                                    list.get(i).put("lHand", list.get(i - 1).getString("lHand"));

                            if (list.get(i).getString("chest").equals("0"))
                                if (!list.get(i - 1).getString("chest").equals("0"))
                                    list.get(i).put("chest", list.get(i - 1).getString("chest"));

                            if (list.get(i).getString("waist").equals("0"))
                                if (!list.get(i - 1).getString("waist").equals("0"))
                                    list.get(i).put("waist", list.get(i - 1).getString("waist"));

                            if (list.get(i).getString("butt").equals("0"))
                                if (!list.get(i - 1).getString("butt").equals("0"))
                                    list.get(i).put("butt", list.get(i - 1).getString("butt"));

                            if (list.get(i).getString("lLeg").equals("0"))
                                if (!list.get(i - 1).getString("lLeg").equals("0"))
                                    list.get(i).put("lLeg", list.get(i - 1).getString("lLeg"));

                            if (list.get(i).getString("rLeg").equals("0"))
                                if (!list.get(i - 1).getString("rLeg").equals("0"))
                                    list.get(i).put("rLeg", list.get(i - 1).getString("rLeg"));

                            if (list.get(i).getString("calves").equals("0"))
                                if (!list.get(i - 1).getString("calves").equals("0"))
                                    list.get(i).put("calves", list.get(i - 1).getString("calves"));

                        }



                    days = new JSONArray();
                    for(int j=0; j<list.size(); j++)
                        days.put(list.get(j));

                    jsn = new JSONObject();
                    jsn.put("days", days);

                    //currentTime.setText(jsn.toString());
                    FileOutputStream out = new FileOutputStream(f);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(jsn.toString());
                    outObject.flush();
                    out.getFD().sync();
                    outObject.close();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setminiAvatar() {
        postav = new Post();
        postav.setListener(this);
        String args[] = new String[3];
        setavatar = true;
        args[0] = "http://caloriesdiary.ru/users/get_avatar";  //аргументы для пост запроса
        args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
        args[2] = FirebaseInstanceId.getInstance().getToken();
        postav.execute(args);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Post getRandomFood = new Post();
        getRandomFood.setListener(this);
        getRandomFood.execute("http://caloriesdiary.ru/calories/get_random_food_acts");
        setminiAvatar();

        try{
           // Toast.makeText(this, getRandomFood.get().toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e){

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            onProfileClick();
//        } else if (id == R.id.nav_rar) {
//            Intent intent = new Intent(getApplicationContext(),ArchiveActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  void onTodayClc(View view){

        try {
            Post log = new Post();

            String args[] = new String[3];

            args[0] = "http://caloriesdiary.ru/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[2] = FirebaseInstanceId.getInstance().getToken();

            log.setListener(this);
            log.execute(args); // вызываем запрос
            JSONObject JSans = log.get();


            if(JSans.getString("status").equals("0")) {
                Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
            else if(JSans.getString("status").equals("1"))
            {
                Intent intent = new Intent(getApplicationContext(), TodayActivity.class);

                startActivity(intent);
            }

        } catch (Exception e){
            Toast.makeText(this, "New intent error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onFoodCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),FoodBuilderActivity.class);
        startActivity(intent);
    }

    public void onStatClc(View view){
        Intent intent = new Intent(getApplicationContext(),StatActivity.class);

        startActivity(intent);
    }

    public void onActionsCatalogClc(View view){
        Intent intent = new Intent(getApplicationContext(),RecycleActionCatalogActivity.class);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    protected void onProfileClick(){
        try
        {
            Post log = new Post();

            String args[] = new String[3];

            args[0] = "http://caloriesdiary.ru/users/get_user_chars";  //аргументы для пост запроса
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID",0));
            args[2] = FirebaseInstanceId.getInstance().getToken();

            log.setListener(this);
            log.execute(args); // вызываем запрос
            JSONObject JSans = log.get();


            if(JSans.getString("status").equals("0")) {
                Intent intent = new Intent(getApplicationContext(), PersonalProfileEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
            else if(JSans.getString("status").equals("1"))
            {
                Intent intent = new Intent(getApplicationContext(), PersonalProfileActivity.class);
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void onDiaryClick (View view)
    {
       Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);

        startActivity(intent);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                //Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                //Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_UP) :
                //Toast.makeText(getApplicationContext(),"3",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                //Toast.makeText(getApplicationContext(),"4",Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                //Toast.makeText(getApplicationContext(),"5",Toast.LENGTH_SHORT).show();
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
    private String getMonth(int i){
        String s="";
        switch (i){
            case Calendar.JANUARY:
                s = "Января";
                break;
            case Calendar.FEBRUARY:
                s = "Февраля";
                break;
            case Calendar.MARCH:
                s = "Марта";
                break;
            case Calendar.APRIL:
                s = "Апреля";
                break;
            case Calendar.MAY:
                s = "Мая";
                break;
            case Calendar.JUNE:
                s = "Июня";
                break;
            case Calendar.JULY:
                s = "Июля";
                break;
            case Calendar.AUGUST:
                s = "Августа";
                break;
            case Calendar.SEPTEMBER:
                s = "Сентября";
                break;
            case Calendar.OCTOBER:
                s = "Октября";
                break;
            case Calendar.NOVEMBER:
                s = "Ноября";
                break;
            case Calendar.DECEMBER:
                s = "Декабря";
                break;
        }

        return s;
    }

    @Override
    public void callback() {

        try
        {
            if(setavatar)
            {
                JSONObject JSans = postav.get();
                //Toast.makeText(this, JSans.toString(), Toast.LENGTH_LONG).show();
                String avatar = JSans.getJSONObject("photo").getString("avatar");
                GetImage getImage = new GetImage();
                getImage.execute(avatar);
                Drawable dr = getImage.get();
                Bitmap bm = ((BitmapDrawable) dr).getBitmap();
                Bitmap crbm = getCroppedBitmap(bm);
                Drawable drawable = (Drawable)new BitmapDrawable(crbm);
                userAvatar.setImageDrawable(drawable);
                //userAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setavatar = false;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}

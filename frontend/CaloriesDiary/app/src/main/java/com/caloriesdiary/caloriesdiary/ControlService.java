package com.caloriesdiary.caloriesdiary;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;

public class ControlService extends Service {

    SharedPreferences sharedPref = null;

    public ControlService() {

        //Запуск сервиса производится:

        //Intent intent = new Intent(this, ControlService.class);
        //startService(intent);

        //Чтобы убить службу, stopSelf() или stopService().

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            sharedPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
            Post log = new Post();

            String[] args = new String[4];
            args[0] = "http://94.130.12.179/users/send_push";
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));

            if (checkCalories()) {
                args[2] = "Ты чё жрёшь, хватит!";
                args[3] = "Иди спортом позанимайся";
            } else {
                args[2] = "Ты чё не жрёшь!";
                args[3] = "Жри давай";
            }

            log.execute(args);
            String resp = log.get().toString();

            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(intent, flags, startId);

    }

    public boolean checkCalories() {
            int sumFood = 0;
            int sumSport = 0;
        try {

            File f = new File(getCacheDir(), "Food.txt");
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inObject = new ObjectInputStream(in);
            String text = inObject.readObject().toString();
            inObject.close();

            JSONObject jsn = new JSONObject(text);
            JSONArray jsonArray = jsn.getJSONArray("food");


            for (int j = 0; j < jsonArray.length(); j++) {
                sumFood += Integer.parseInt(jsonArray.getJSONObject(j).get("calories").toString());
            }

            f = new File(getCacheDir(), "Actions.txt");
            in = new FileInputStream(f);
            inObject = new ObjectInputStream(in);
            text = inObject.readObject().toString();
            inObject.close();

            jsn = new JSONObject(text);
            jsonArray = jsn.getJSONArray("active");

            for (int j = 0; j < jsonArray.length(); j++) {
                sumSport += Integer.parseInt(jsonArray.getJSONObject(j).get("calories").toString());
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return sumSport > sumFood;
    }
}

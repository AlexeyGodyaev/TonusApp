package com.caloriesdiary.caloriesdiary;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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

        checkCalories();
        return super.onStartCommand(intent, flags, startId);

    }

    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.human_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void checkCalories() {
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
            stopSelf();
        }

        if (sumSport > sumFood)
        {
            sendNotification("Поешь", "Недостаточно каллорий");
        }
        else
        {
            sendNotification("Иди спортом позанимайся", "Избыток каллорий");
        }

    }
}

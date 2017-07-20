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
    public IBinder onBind(Intent intent)
    {
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            sharedPref = getSharedPreferences("GlobalPref",MODE_PRIVATE);
            Post log = new Post();

            String[] args = new String[4];
            args[0] = "http://94.130.12.179/users/send_push";
            args[1] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
            args[2] = "Ты чё жрёшь, хватит!";
            args[3] = "Иди спортом позанимайся";

            log.execute(args);
            String resp = log.get().toString();

            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            //stopSelf();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(intent, flags, startId);

    }

}

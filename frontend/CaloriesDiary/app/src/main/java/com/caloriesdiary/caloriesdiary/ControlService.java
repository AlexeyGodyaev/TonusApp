package com.caloriesdiary.caloriesdiary;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.net.MalformedURLException;

public class ControlService extends Service {
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
            Toast.makeText(this, "Service !Control! online", Toast.LENGTH_SHORT).show();

            stopSelf();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(intent, flags, startId);

    }

}

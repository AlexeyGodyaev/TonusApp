package com.caloriesdiary.caloriesdiary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GetToken extends FirebaseInstanceIdService {
    public GetToken() {

    }

    @Override
    public void onTokenRefresh() {


    }

}

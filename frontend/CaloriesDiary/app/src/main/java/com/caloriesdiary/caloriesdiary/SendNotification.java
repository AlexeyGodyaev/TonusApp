package com.caloriesdiary.caloriesdiary;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SendNotification extends AsyncTask<String, Void, String> {

    String API_key = "AIzaSyBncLGTJqMZJu96vJYvhvSM1Lpekr2m2iw";


    protected String doInBackground(String... arg0) {

        try {
            JSONObject js = new JSONObject();
            URL url = new URL("https://fcm.googleapis.com/fcm/send");

            JSONObject postDataParams = new JSONObject();

            postDataParams.put("title", arg0[0]);   // Notification title
            postDataParams.put("body", arg0[1]); // Notification body
            postDataParams.put("to", FirebaseInstanceId.getInstance().getToken());


            HttpURLConnection urlConnection;

            String result = null;

            //Connect
            urlConnection = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "key=" + API_key);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataParams.toString()); // преобразуем json объект в строку параметров запроса

            writer.flush();
            writer.close();
            os.close();

            int responseCode=urlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in=new BufferedReader(
                        new InputStreamReader(
                                urlConnection.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer("");
                String line="";


                while((line = in.readLine()) != null) {
                    js = new JSONObject(line);
                    sb.append(js.toString());
                    break;
                }

                in.close();

                return js.toString();

            }
            else {
                return js.toString();
            }
        }
        catch(Exception e){
            return e.toString();
        }
    }


    protected void onPostExecute(String result) {

    }

}




package com.caloriesdiary.caloriesdiary.Posts;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Get extends AsyncTask<String,Void,JSONObject>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... inputparams) {

        JSONObject jsonObject;
        try
        {
            URL url = new URL(inputparams[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int responseCode=conn.getResponseCode();


            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                if((line = in.readLine()) != null) {

                    jsonObject = new JSONObject(line);
                    return jsonObject;
                }
                else
                {
                    jsonObject = new JSONObject();
                    jsonObject.put("status","0");
                    jsonObject.put("msg","readLine is null");
                    return jsonObject;
                }


            }
            else
            {
                jsonObject = new JSONObject();
                jsonObject.put("status","0");
                jsonObject.put("msg","Wrong response code: " + String.valueOf(responseCode));
            }



        }
        catch(Exception e)
        {
            try
            {
                jsonObject = new JSONObject();
                jsonObject.put("status","0");
                jsonObject.put("msg","Exception error: " + e.toString());
                return jsonObject;
            }
            catch (Exception e1)
            {

            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
    }
}

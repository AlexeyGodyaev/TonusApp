package com.caloriesdiary.caloriesdiary.HTTP;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;


public class SaveTodayParams extends AsyncTask<JSONObject, Void, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {

        try {
            String resp = "";
            URL url = new URL("http://caloriesdiary.ru/users/save_day");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(15000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(jsonObjects[0])); // преобразуем json объект в строку параметров запроса
            Log.e("gjc",(jsonObjects[0].toString()));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){

                BufferedReader in=new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;


                if((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                resp = sb.toString();
            }
            else resp = connection.getResponseMessage();

            return resp;

        } catch (Exception e) {
            return e.toString();
        }
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }

        return result.toString();
    }
}

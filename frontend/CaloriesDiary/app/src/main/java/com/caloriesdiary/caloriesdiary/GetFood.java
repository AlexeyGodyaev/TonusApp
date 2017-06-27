package com.caloriesdiary.caloriesdiary;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class GetFood extends AsyncTask<String, Void, String>{
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {

        try {

            URL url = new URL(strings[0]); // первый аргумент из массива который передан при вызове

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in=new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer("");
                String line="";


                while((line = in.readLine()) != null) {
                    JSONObject js = new JSONObject(line);
                    JSONArray jArr = js.getJSONArray("food");
                    for(int i=0;i<jArr.length();i++) {
                        sb.append(jArr.getJSONObject(i).getString("id"));
                        sb.append(',');
                        sb.append(jArr.getJSONObject(i).getString("name"));
                        sb.append(',');
                        sb.append(jArr.getJSONObject(i).getString("protein"));
                        sb.append(',');
                        sb.append(jArr.getJSONObject(i).getString("fats"));
                        sb.append(',');
                        sb.append(jArr.getJSONObject(i).getString("carbs"));
                        sb.append(',');
                        sb.append(jArr.getJSONObject(i).getString("calories"));
                        sb.append(';');
                    }
                    break;
                }


                in.close();
                return sb.toString();

            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }

    }
}

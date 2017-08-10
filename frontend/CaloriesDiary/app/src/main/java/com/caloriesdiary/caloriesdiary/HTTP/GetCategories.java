package com.caloriesdiary.caloriesdiary.HTTP;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetCategories extends AsyncTask<String, Void, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        try
        {
            URL url = new URL(strings[0]);
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
                StringBuilder sb = new StringBuilder("");
                String line;
                String ans = "emp";

                if((line = in.readLine()) != null) {
                    JSONObject js = new JSONObject(line);
                    JSONArray jArr = js.getJSONArray("categories");
                    ans = jArr.toString();
                }

                in.close();
                return ans;
            }
            else {
                return "false : "+ responseCode;
            }
        }
        catch (Exception e)
        {
            return "Exception:" + e.toString();
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

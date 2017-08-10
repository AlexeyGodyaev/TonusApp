package com.caloriesdiary.caloriesdiary.HTTP;

import android.os.AsyncTask;

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

public class GetDays extends AsyncTask<String, Void, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            String resp = "";
            URL url = new URL("http://caloriesdiary.ru/users/get_days");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(15000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            JSONObject postDataParams = new JSONObject();

            postDataParams.put("id", strings[0]);
            postDataParams.put("instanceToken", strings[1]);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams)); // преобразуем json объект в строку параметров запроса

            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){

                BufferedReader in=new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();


                if((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                resp = line;
                return resp;
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

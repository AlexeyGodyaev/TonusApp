package com.caloriesdiary.caloriesdiary;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.data;

public class Post extends AsyncTask<String, Void, JSONObject> {

        protected void onPreExecute(){}

        protected JSONObject doInBackground(String... arg0) {
            JSONObject js = new JSONObject();
            try {

                URL url = new URL(arg0[0]); // первый аргумент из массива который передан при вызове
                JSONObject postDataParams = new JSONObject();

                switch (arg0[0]) {
                    case "http://94.130.12.179/users/save_goal":
                        postDataParams.put("id",arg0[1]);
                        postDataParams.put("desired_weight",arg0[2]);
                        postDataParams.put("period",arg0[3]);
                        postDataParams.put("goal",arg0[5]);
                        postDataParams.put("activityType",arg0[4]);
                        break;
                    case "http://94.130.12.179/users/auth":
                        postDataParams.put("username", arg0[1]);//далее по массиву
                        postDataParams.put("password", arg0[2]);
                        break;
                    case "http://94.130.12.179/users/register":
                        postDataParams.put("username", arg0[1]);//далее по массиву
                        postDataParams.put("password", arg0[2]);
                        postDataParams.put("email", arg0[3]);
                        break;
                    case "http://94.130.12.179/users/save_user_chars":
                        postDataParams.put("id",arg0[1]);
                        postDataParams.put("realName",arg0[2]);
                        postDataParams.put("weight",arg0[3]);
                        postDataParams.put("height",arg0[4]);
                        postDataParams.put("sex",arg0[5]);
                        postDataParams.put("age",arg0[6]);
                        postDataParams.put("activityType",arg0[7]);
                        postDataParams.put("avg_dream",arg0[8]);
                        postDataParams.put("wokeup_time",arg0[9]);
                        break;
                    case "http://94.130.12.179/users/forgot_password":
                        postDataParams.put("email",arg0[1]);
                        break;
                    case "http://94.130.12.179/users/get_user_chars":
                        postDataParams.put("id",arg0[1]);
                        break;
                    case "http://94.130.12.179/users/delete":
                        postDataParams.put("id",arg0[1]);
                        postDataParams.put("password",arg0[2]);
                        break;
                    case "http://94.130.12.179/users/change_password":
                        postDataParams.put("username",arg0[1]);
                        postDataParams.put("oldpassword",arg0[2]);
                        postDataParams.put("newpassword",arg0[3]);
                        break;


                }


                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams)); // преобразуем json объект в строку параметров запроса

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream(), "UTF-8"));
                    StringBuffer sb = new StringBuffer("");
                    String line="";


                    while((line = in.readLine()) != null) {
                        js = new JSONObject(line);
                        sb.append(js.getString("msg"));
                        break;
                    }

                    in.close();
                    //return sb.toString();
                    return js;

                }
                else {
                    //return new String("false : "+responseCode);
                    return js;
                }
            }
            catch(Exception e){
                //return new String("Exception: " + e.getMessage());
                return js;
            }

        }


        protected void onPostExecute(String result) {

    }

    public String getPostDataString(JSONObject params) throws Exception {

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


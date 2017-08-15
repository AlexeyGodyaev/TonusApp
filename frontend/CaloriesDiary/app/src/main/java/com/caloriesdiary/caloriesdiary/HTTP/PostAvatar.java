package com.caloriesdiary.caloriesdiary.HTTP;


import android.graphics.Bitmap;
import android.provider.Settings;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostAvatar {
    String user_id,instanceToken;
    Bitmap avatar;
    String attachmentFileName = "";
    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String attachmentName = "avatar";

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    int responseCode;
    StringBuilder result;

    public JSONObject PostAvatar(String id, String token, Bitmap bitmap)
    {
        this.user_id = id;
        this.instanceToken = token;
        this.avatar = bitmap;

        try
        {
            System.setProperty("http.keepAlive", "false");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            attachmentFileName = "bit.png";
            byte[] imageBytes = baos.toByteArray();

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(imageBytes);

            URL urlobject = new URL("http://caloriesdiary.ru/users/set_avatar");
            conn = (HttpURLConnection) urlobject.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs

            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Encoding", "");
            //conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", "profile_picture");
            conn.setRequestProperty("user_id", user_id);
            conn.setRequestProperty("instanceToken", instanceToken);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\"" + lineEnd + lineEnd
                    + user_id + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"instanceToken\"" + lineEnd + lineEnd
                    + instanceToken + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + lineEnd);

                    /*dos.writeBytes("Content-Disposition: form-data; name=\"avatar\";filename=\""
                            + "profile_picture" + "\"" + lineEnd);*/
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            responseCode = conn.getResponseCode();

            String line;
            JSONObject json;
            if (responseCode == 200  ) {
                BufferedReader in=new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream(), "UTF-8"));
                if((line = in.readLine()) != null)
                {
                    json = new JSONObject(line);

                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    return json;

                }

            }
            else {

                    json = new JSONObject();
                    json.put("msg",String.valueOf(responseCode));
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    return json;


            }



        }
        catch (Exception e)
        {
            try
            {
                JSONObject json = new JSONObject();
                json.put("msg",e.toString());
                return json;
            }
            catch (Exception e2)
            {

            }

        }


       return null;
    }
}

package com.caloriesdiary.caloriesdiary.HTTP;


import android.graphics.Bitmap;
import android.provider.Settings;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

    public JSONObject PostAvatar(String id, String token, Bitmap bitmap)
    {
        this.instanceToken = id;
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

            


        }
        catch (Exception e)
        {

        }


       return null;
    }
}

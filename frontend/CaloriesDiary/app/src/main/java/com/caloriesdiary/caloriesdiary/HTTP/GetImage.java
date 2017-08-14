package com.caloriesdiary.caloriesdiary.HTTP;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;


public class GetImage extends AsyncTask<String,Void,Drawable> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Drawable doInBackground(String... strings) {

        try
        {
            InputStream is = (InputStream) new URL(strings[0]).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }
        catch (Exception e)
        {
            return null;
        }


    }

    @Override
    protected void onPostExecute(Drawable aVoid) {
        super.onPostExecute(aVoid);
    }
}

package com.caloriesdiary.caloriesdiary;


import android.icu.util.TimeUnit;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FoodBuilderActivity extends AppCompatActivity {
    TextView textView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_builder_layout);
        textView = (TextView) findViewById(R.id.test_view);
        progressBar = (ProgressBar) findViewById(R.id.test_progressbar);
        MyAsync myAsync = new MyAsync();
        myAsync.execute();



    }
    public void push(View view)
    {
        Snackbar.make(view,"push",Snackbar.LENGTH_LONG).show();
    }
    class MyAsync extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("Begin");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                Thread.sleep(50000);
            }
            catch (Exception e)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            textView.setText("End");
            super.onPostExecute(aVoid);
        }
    }
}

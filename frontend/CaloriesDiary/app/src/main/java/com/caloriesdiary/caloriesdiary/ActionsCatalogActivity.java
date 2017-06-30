package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActionsCatalogActivity extends FragmentActivity {

    EditText srch;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actions_catalog_layout);

        srch = (EditText) findViewById(R.id.srchAction);
        srch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (i == KeyEvent.KEYCODE_ENTER)){
                    //высылаем пост с именем
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.actionsList);

        ActionsAdapter adapter = new ActionsAdapter(this, initData());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput("jkh.txt",MODE_APPEND)));

                    writer.write("its works");
                    writer.close();

                    FileInputStream fin = null;
                    fin = openFileInput("jkh.txt");
                    byte[] bytes = new byte[fin.available()];
                    fin.read(bytes);
                    String text = new String (bytes);

                    Toast.makeText(getApplicationContext(), text , Toast.LENGTH_LONG).show();
                }
                catch (FileNotFoundException fEx){

                }
                catch (IOException iEx){

                }
            }
        });
    }

    public  String getAction() throws InterruptedException, ExecutionException {
        GetActions get = new GetActions();
        get.execute("http://192.168.1.205/activities/get_activities");

        return get.get().toString();
    }

    private List<ActionItem> initData() {
        List<ActionItem> list = new ArrayList<ActionItem>();
        String resp = null;
        String actionName;
        Float calories=new Float(0);
        Integer id=0;

        try {
            resp = getAction();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resp != null)
            while(resp.length()>10){
                try {
                    Integer f1 = new Integer(resp.substring(0,resp.indexOf(',')));
                    id=f1;
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат строки!");
                }
                resp=resp.substring(resp.indexOf(',')+1);

                actionName = resp.substring(0,resp.indexOf(','));
                resp=resp.substring(resp.indexOf(',')+1);

                try {
                    Float f1 = new Float(resp.substring(0,resp.indexOf(';')));
                    calories=f1;
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат строки!");
                }

                resp=resp.substring(resp.indexOf(';')+1);

                if(calories!=0)
                    list.add(new ActionItem(actionName, calories, id));
            }

        return list;
    }
}

package com.caloriesdiary.caloriesdiary;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;


public class FoodBuilderActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_builder_layout);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup();
        TabHost.TabSpec tabSpec;

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("tag1");
        // название вкладки
        tabSpec.setIndicator("Вкладка 1");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.tab1);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        // указываем название и картинку
        // в нашем случае вместо картинки идет xml-файл,
        tabSpec.setIndicator("Вкладка 2");
        // который определяет картинку по состоянию вкладки
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onAddFood (View view)
    {
        View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_food_dialoglayout,null);
        final EditText edittext = (EditText) content.findViewById(R.id.custom_food_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodBuilderActivity.this);
        builder.setTitle("Добавление блюда")
                .setView(content)
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(FoodBuilderActivity.this, edittext.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}

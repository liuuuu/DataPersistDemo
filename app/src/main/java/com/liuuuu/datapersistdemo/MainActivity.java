package com.liuuuu.datapersistdemo;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    Class[] classes = {SettingsActivity.class, HomeActivity.class, FormActivity.class,
            ActivityOne.class, ActivityTwo.class, InternalActivity.class, ExternalActivity.class};
    String[] titles = {"PreferenceActivity实战", "加载默认首选项的Activity", "简单数据存储",
            "通用\tSharedPreferences(存)", "通用\tSharedPreferences(取)", "内部存储", "外部存储"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, titles);

        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, classes[position]);
        startActivity(intent);
    }
}

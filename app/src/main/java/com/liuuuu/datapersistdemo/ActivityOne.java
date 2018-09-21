package com.liuuuu.datapersistdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityOne extends AppCompatActivity {
    public static final String PREF_NAME = "myPreferences";
    public static final String PREF_KEY_DATA = "dataKey";
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        mPreferences.edit().putString(PREF_KEY_DATA, "Hello World!!!").commit();
        TextView textView = new TextView(this);
        textView.setText("存入数据\"Hello World!!!\"");
        setContentView(textView);
    }
}

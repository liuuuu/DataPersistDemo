package com.liuuuu.datapersistdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ActivityTwo extends AppCompatActivity {

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(ActivityOne.PREF_NAME, Activity.MODE_PRIVATE);
        TextView textView = new TextView(this);
        textView.setText("取出数据：" + mPreferences.getString(ActivityOne.PREF_KEY_DATA, "获取失败！"));
        setContentView(textView);
    }
}

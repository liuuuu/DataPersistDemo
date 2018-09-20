package com.liuuuu.datapersistdemo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 加载默认首选项
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 访问当前设置
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        String name = settings.getString("namePref", "");
        boolean isMoreEnable = settings.getBoolean("morePref", false);
        Log.i("●  TAG", "onResume: name = " + name + " isMoreEnable = " + isMoreEnable);
    }
}

package com.liuuuu.datapersistdemo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.activity_home);
        // 加载默认首选项
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        */

        // 以 PreferenceFragment 的形式引入了一种新的创建首选项界面的方式。 API>=11
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, new SettingsFragment());
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        // 访问当前设置
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String name = settings.getString("namePref", "");
        boolean isMoreEnable = settings.getBoolean("morePref", false);
        Log.i("●  TAG", "onResume: name = " + name + " isMoreEnable = " + isMoreEnable);
        */
    }
}

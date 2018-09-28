package com.liuuuu.datapersistdemo;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初次启动时加载首选项默认值
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // 从 XML 中加载首选项数据
        addPreferencesFromResource(R.xml.preferences);
    }
}

package com.liuuuu.datapersistdemo;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从 XML 中加载首选项数据
        addPreferencesFromResource(R.xml.settings);
    }
}

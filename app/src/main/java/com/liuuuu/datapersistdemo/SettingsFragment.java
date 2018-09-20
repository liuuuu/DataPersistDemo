package com.liuuuu.datapersistdemo;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从 XML 中加载设置数据
        addPreferencesFromResource(R.xml.settings);

    }

}

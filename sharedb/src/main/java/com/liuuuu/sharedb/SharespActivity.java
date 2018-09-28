package com.liuuuu.sharedb;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class SharespActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String SETTINGS_ACTION = "com.liuuuu.datapersistdemo.ACTION_SETTINGS";
    public static final Uri SETTINGS_CONTENT_URI = Uri.parse("content://com.liuuuu.datapersistdemo.settingsprovider/settings");

    public static class SettingsColumns {
        public static final String _ID = Settings.NameValueTable._ID;
        public static final String NAME = Settings.NameValueTable.NAME;
        public static final String VALUE = Settings.NameValueTable.VALUE;
    }

    TextView mEnable, mName, mSelection;
    CheckBox mToggle;

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updatePreferences();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharesp);

        mEnable = findViewById(R.id.value_enabled);
        mName = findViewById(R.id.value_name);
        mSelection = findViewById(R.id.value_selection);
        mToggle = findViewById(R.id.checkbox_enable);
        mToggle.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获得最新的提供程序数据
        updatePreferences();
        // 当 Activity 可见时，注册一个观察者来监听数据的变化
        getContentResolver().registerContentObserver(SETTINGS_CONTENT_URI, false, mObserver);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ContentValues cv = new ContentValues(2);
        cv.put(SettingsColumns.NAME, "preferenceEnabled");
        cv.put(SettingsColumns.VALUE, isChecked);

        //更新提供程序，这会触发我们的观察者
        getContentResolver().update(SETTINGS_CONTENT_URI, cv, null, null);
    }

    public void onSettingsClick(View view) {
        try {
            Intent intent = new Intent(SETTINGS_ACTION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "没有可响应的程序", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePreferences() {
        Cursor c = getContentResolver().query(SETTINGS_CONTENT_URI,
                new String[]{SettingsColumns.NAME, SettingsColumns.VALUE},
                null, null, null);
        if (c == null) {
            return;
        }

        while (c.moveToNext()) {
            String key = c.getString(0);
            if ("preferenceEnabled".equals(key)) {
                mEnable.setText(String.format("Enabled Setting = %s", c.getString(1)));
                mToggle.setChecked(Boolean.parseBoolean(c.getString(1)));
            } else if ("preferenceName".equals(key)) {
                mName.setText(String.format("User Name Setting = %s", c.getString(1)));
            } else if ("preferenceSelection".equals(key)) {
                mSelection.setText(String.format("Selection Setting = %s", c.getString(1)));
            }
        }
        c.close();
    }

}

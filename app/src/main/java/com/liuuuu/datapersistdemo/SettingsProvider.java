package com.liuuuu.datapersistdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

public class SettingsProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.liuuuu.datapersistdemo.settingsprovider/settings");

    public static class Columns {
        public static final String _ID = Settings.NameValueTable._ID;
        public static final String NAME = Settings.NameValueTable.NAME;
        public static final String VALUE = Settings.NameValueTable.VALUE;
    }

    private static final String NAME_SELECTION = Columns.NAME + " = ?";

    private SharedPreferences mPreferences;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(projection);
        Map<String, ?> preferences = mPreferences.getAll();
        Set<String> preferenceKeys = preferences.keySet();

        if (TextUtils.isEmpty(selection)) {
            // 获取所有条目
            for (String key :
                    preferenceKeys) {
                // 仅插入请求字段
                MatrixCursor.RowBuilder builder = cursor.newRow();
                for (String column :
                        projection) {
                    if (column.equals(Columns._ID)) {
                        // 生成唯一的 id
                        builder.add(key.hashCode());
                    }
                    if (column.equals(Columns.NAME)) {
                        builder.add(key);
                    }
                    if (column.equals(Columns.VALUE)) {
                        builder.add(preferences.get(key));
                    }
                }
            }
        } else if (selection.equals(NAME_SELECTION)) {
            // 解析键值并检查它是否存在
            String key = selectionArgs == null ? "" : selectionArgs[0];
            if (preferences.containsKey(key)) {
                // 得到需要的条目
                MatrixCursor.RowBuilder builder = cursor.newRow();
                for (String column :
                        projection) {
                    if (column.equals(Columns._ID)) {
                        // 生成唯一的 id
                        builder.add(key.hashCode());
                    }
                    if (column.equals(Columns.NAME)) {
                        builder.add(key);
                    }
                    if (column.equals(Columns.VALUE)) {
                        builder.add(preferences.get(key));
                    }
                }
            }
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // 检查键是否存在，并更新它的值
        String key = values.getAsString(Columns.NAME);
        if (mPreferences.contains(key)) {
            Object value = values.get(Columns.VALUE);
            SharedPreferences.Editor editor = mPreferences.edit();
            if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Number) {
                editor.putFloat(key, ((Number) value).floatValue());
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            } else {
                // 无效的值，不更新
                return 0;
            }
            editor.commit();
            // 通知观察者
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            return 1;
        }
        // 键不在首选项中
        return 0;
    }
}

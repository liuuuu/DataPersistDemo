package com.liuuuu.datapersistdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SharedDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "frienddb";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "friends";
    public static final String COL_FIRST = "firstName";
    public static final String COL_LAST = "lastName";
    public static final String COL_PHONE = "phoneNumber";

    private static final String STRING_CREATE =
            String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT);",
                    TABLE_NAME, COL_FIRST, COL_LAST, COL_PHONE);

    public SharedDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库
        db.execSQL(STRING_CREATE);

        // 向数据库中插入示例值
        ContentValues cv = new ContentValues(3);
        cv.put(COL_FIRST, "Zhang");
        cv.put(COL_LAST, "San");
        cv.put(COL_PHONE, 88881234);
        db.insert(TABLE_NAME, null, cv);
        cv = new ContentValues(3);
        cv.put(COL_FIRST, "Li");
        cv.put(COL_LAST, "Si");
        cv.put(COL_PHONE, 88882345);
        db.insert(TABLE_NAME, null, cv);
        cv = new ContentValues(3);
        cv.put(COL_FIRST, "Wang");
        cv.put(COL_LAST, "Wu");
        cv.put(COL_PHONE, 88883456);
        db.insert(TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 目前清空并重新创建数据库
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

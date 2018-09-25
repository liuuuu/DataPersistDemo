package com.liuuuu.datapersistdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "people";
    public static final String COL_NAME = "pName";
    public static final String COL_DATE = "pDate";

    private static final String STRING_CREATE = String.format(
            "CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s DATE);",
            TABLE_NAME, COL_NAME, COL_DATE);

    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库
        db.execSQL(STRING_CREATE);

        // 可能还需要在这里加载数据库的初始值
        ContentValues cv = new ContentValues(2);
        cv.put(COL_NAME, "Shen Liuuuu");
        // 格式化 SQL 日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cv.put(COL_DATE, dateFormat.format(new Date()));// 插入对当前日期数据
        db.insert(TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        // 目前清空并重新创建数据库
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        */
        // 在 v1 的基础进行升级。添加电话号码字段
        if (oldVersion <= 1) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN phone_number INTEGER;");
        }
        // 在 v2 的基础上进行升级。添加日期输入字段
        if (oldVersion <= 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN entry_date DATE");
        }
    }
}

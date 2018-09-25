package com.liuuuu.datapersistdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DbActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    EditText mText;
    Button mAdd;
    ListView mList;

    MyDbHelper mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        mText = findViewById(R.id.name);
        mAdd = findViewById(R.id.add);
        mAdd.setOnClickListener(this);
        mList = findViewById(R.id.list);
        mList.setOnItemClickListener(this);

        mHelper = new MyDbHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 建立和数据库的连接
        mDb = mHelper.getWritableDatabase();
        String[] columns = new String[]{"_id", MyDbHelper.COL_NAME, MyDbHelper.COL_DATE};
        mCursor = mDb.query(MyDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        // 更新列表
        String[] headers = new String[]{MyDbHelper.COL_NAME, MyDbHelper.COL_DATE};
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                mCursor,
                headers,
                new int[]{android.R.id.text1, android.R.id.text2});
        mList.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭连接
        mDb.close();
        mCursor.close();
    }

    @Override
    public void onClick(View v) {
        // 向数据库中添加新数据
        ContentValues cv = new ContentValues(2);
        cv.put(MyDbHelper.COL_NAME, mText.getText().toString());
        // 格式化 SQL 日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 插入当前日期数据
        cv.put(MyDbHelper.COL_DATE, dateFormat.format(new Date()));
        mDb.insert(MyDbHelper.TABLE_NAME, null, cv);
        // 更新列表
        mCursor.requery();
        mAdapter.notifyDataSetChanged();
        // 清空编辑字段
        mText.setText(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 删除数据库中的条目
        mCursor.moveToPosition(position);
        // 获得该行的 ID 值
        String rowId = mCursor.getString(0);// Cursor 的字段 0 是 ID
        mDb.delete(MyDbHelper.TABLE_NAME, "_id = ?", new String[]{rowId});
        // 更新列表
        mCursor.requery();
        mAdapter.notifyDataSetChanged();
    }
}

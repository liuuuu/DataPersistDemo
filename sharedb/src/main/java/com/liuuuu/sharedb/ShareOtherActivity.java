package com.liuuuu.sharedb;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ShareOtherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int LOADER_LIST = 100;

    SimpleCursorAdapter mAdapter;

    public static final Uri CONTENT_URI = Uri.parse("content://com.liuuuu.datapersistdemo.imageprovider");
    public static final String COLUMN_NAME = "nameString";
    public static final String COLUMN_IMAGE = "imageUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_LIST, null, this);
        setContentView(R.layout.activity_share_other);

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[]{COLUMN_NAME}, new int[]{android.R.id.text1}, 0);

        ListView listView = findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 得到 selection 的游标
        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        // 加载 name 字段到 TextView
        TextView textView = findViewById(R.id.name);
        textView.setText(c.getString(1));

        ImageView imageView = findViewById(R.id.image);
        try {
            // 从 image 字段中加载内容到 ImageView 中
            InputStream is = getContentResolver().openInputStream(Uri.parse(c.getString(2)));
            Bitmap image = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{"_id", COLUMN_NAME, COLUMN_IMAGE};
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}

package com.liuuuu.datapersistdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.liuuuu.datapersistdemo.imageprovider");

    public static final String COLUMN_NAME = "nameString";
    public static final String COLUMN_IMAGE = "imageUri";

    private String[] mNames;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("该 ContentProvider 为只读。");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("该 ContentProvider 为只读。");
    }

    @Override
    public boolean onCreate() {
        mNames = new String[]{"Zhang san", "Li si", "Wang wu"};
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(projection);
        for (int i = 0; i < mNames.length; i++) {
            // 只插入请求的字段
            MatrixCursor.RowBuilder builder = cursor.newRow();
            for (String column :
                    projection) {
                if (column.equals("_id")) {
                    // 使用数组索引作为唯一id
                    builder.add(i);
                }
                if (column.equals(COLUMN_NAME)) {
                    builder.add(mNames[i]);
                }
                if (column.equals(COLUMN_IMAGE)) {
                    builder.add(Uri.withAppendedPath(CONTENT_URI, String.valueOf(i)));
                }
            }
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("该 ContentProvider 为只读。");
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        int requested = Integer.parseInt(uri.getLastPathSegment());
        AssetFileDescriptor afd;
        AssetManager manager = getContext().getAssets();
        // 为请求项返回正确的 assets 资源
        try {
            switch (requested) {
                case 0:
                    afd = manager.openFd("logo1.png");
                    break;
                case 1:
                    afd = manager.openFd("logo2.png");
                    break;
                case 2:
                    afd = manager.openFd("logo3.png");
                    break;
                default:
                    afd = manager.openFd("logo1.png");
            }
            return afd;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

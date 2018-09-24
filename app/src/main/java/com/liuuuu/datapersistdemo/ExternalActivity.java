package com.liuuuu.datapersistdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExternalActivity extends AppCompatActivity {

    private static final String FILENAME = "data.txt";
    private static final String DNAME = "myfiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        setContentView(tv);

        // 在外部存储器中创建一个新的目录
        File rootPath = new File(Environment.getExternalStorageDirectory(), DNAME);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        // 创建文件的引用
//        File dataFile = new File(Environment.getExternalStorageDirectory(), FILENAME);
        File dataFile = new File(rootPath, FILENAME);

        // 检查外部存储器是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Cannot use storage.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 创建一个新文件并写入一些数据
        try {
            FileOutputStream mOutput = new FileOutputStream(dataFile, false);
            String data = "THIS DATA WRITTEN TO A FILE";
            mOutput.write(data.getBytes());
            mOutput.flush();
            // 使用外部文件时，通常最好同步该文件
            mOutput.getFD().sync();
            mOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取已经创建的文件并显示在屏幕上
        try {
            FileInputStream mInPut = new FileInputStream(dataFile);
            byte[] data = new byte[128];
            mInPut.read(data);
            mInPut.close();

            String display = new String(data);
            tv.setText(display.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("● TAG ●", "onCreate: File Path:" + dataFile.getAbsolutePath());

        // 删除创建的文件
        dataFile.delete();
    }

}

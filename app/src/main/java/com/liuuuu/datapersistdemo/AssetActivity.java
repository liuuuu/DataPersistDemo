package com.liuuuu.datapersistdemo;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AssetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        setContentView(tv);

        try {
            // 访问应用程序的 assets 目录
            AssetManager manager = getAssets();
            // 打开数据文件
            InputStream mInput = manager.open("data.csv");

            // 解析 CSV 数据并显示
            ArrayList<Person> cooked = parse(mInput);
            mInput.close();

            StringBuilder builder = new StringBuilder();
            for (Person piece :
                    cooked) {
                builder.append(String.format("%s is %s years old, and likes the color %s", piece.name, piece.age, piece.color));
                builder.append("\n");
            }
            tv.setText(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* 简单 CSV 解析器 */
    private static final int COL_NAME = 0;
    private static final int COL_AGE = 1;
    private static final int COL_COLOR = 2;

    private ArrayList<Person> parse(InputStream in) throws IOException {
        ArrayList<Person> results = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String nextLine = null;
        while ((nextLine = reader.readLine()) != null) {
            String[] tokens = nextLine.split(",");
            if (tokens.length != 3) {
                Log.w("CSVParser", "Skipping Bad CSV Row");
                continue;
            }
            // 添加新的解析器结果
            Person current = new Person();
            current.name = tokens[COL_NAME];
            current.color = tokens[COL_COLOR];
            current.age = tokens[COL_AGE];

            results.add(current);
        }

        return results;
    }

    private class Person {
        public String name;
        public String age;
        public String color;

        public Person() {
        }
    }
}

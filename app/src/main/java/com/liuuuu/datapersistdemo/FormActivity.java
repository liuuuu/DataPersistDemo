package com.liuuuu.datapersistdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, message;
    CheckBox age;
    Button submit;

    SharedPreferences formStore;

    boolean submitSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        email = findViewById(R.id.email);
        message = findViewById(R.id.message);
        age = findViewById(R.id.age);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        // 获取或创建首选项对象
        formStore = getPreferences(Activity.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 还原表单数据
        email.setText(formStore.getString("email", ""));
        message.setText(formStore.getString("message", ""));
        age.setChecked(formStore.getBoolean("age", false));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (submitSuccess) {
            // 可以同时调用 Editor
            formStore.edit().clear().commit();
        } else {
            // 保存表单数据
            SharedPreferences.Editor editor = formStore.edit();
            editor.putString("email", email.getText().toString());
            editor.putString("message", message.getText().toString());
            editor.putBoolean("age", age.isChecked());
            editor.commit();
        }
    }

    @Override
    public void onClick(View v) {
        // 发送消息

        // 标记操作成功
        submitSuccess = true;
        // 关闭
        finish();
    }
}


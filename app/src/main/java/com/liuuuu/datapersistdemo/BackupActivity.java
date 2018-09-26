package com.liuuuu.datapersistdemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BackupActivity extends AppCompatActivity implements BackupTask.CompletionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Button btnBackup = findViewById(R.id.button_backup);
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbBackup();
            }
        });

        Button btnRestore = findViewById(R.id.button_restore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRestore();
            }
        });
    }

    private void dbRestore() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            BackupTask task = new BackupTask(this);
            task.setCompletionListener(this);
            task.execute(BackupTask.COMMAND_RESTORE);
        }
    }

    private void dbBackup() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            BackupTask task = new BackupTask(this);
            task.setCompletionListener(this);
            task.execute(BackupTask.COMMAND_BACKUP);
        }
    }


    @Override
    public void onBackupComplete() {

        Toast.makeText(this, "Backup Successful", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRestoreComplete() {

        Toast.makeText(this, "Restore Successful", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(int errorCode) {

        if (errorCode == BackupTask.RESTORE_NO_FILE_ERROR) {
            Toast.makeText(this, "No Backup Found to Restore", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error During Operation: " + errorCode, Toast.LENGTH_SHORT).show();
        }

    }
}

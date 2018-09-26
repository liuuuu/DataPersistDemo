package com.liuuuu.datapersistdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BackupTask extends AsyncTask<String, Void, Integer> {

    public interface CompletionListener {
        void onBackupComplete();

        void onRestoreComplete();

        void onError(int errorCode);
    }

    public static final int BACKUP_SUCCESS = 1;
    public static final int RESTORE_SUCCESS = 2;
    public static final int BACKUP_ERROR = 3;
    public static final int RESTORE_NO_FILE_ERROR = 4;

    public static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restoreDatabase";

    private Context mContext;
    private CompletionListener listener;

    public BackupTask(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setCompletionListener(CompletionListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {

        // 获得数据库引用
        File dbFile = mContext.getDatabasePath("mydb");
        // 获得备份的目录位置引用
        File exportDir = new File(Environment.getExternalStorageDirectory(), "myAppBackups");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        Log.i("------TAG------", "exportDir = " + exportDir.getAbsolutePath());
        File backup = new File(exportDir, dbFile.getName());

        // 检查需要的操作
        String command = params[0];
        if (command.equals(COMMAND_BACKUP)) {
            // 尝试复制文件
            try {
                backup.createNewFile();
                fileCopy(dbFile, backup);

                return BACKUP_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
                return BACKUP_ERROR;
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            // 尝试复制文件
            try {
                if (!backup.exists()) {
                    return RESTORE_NO_FILE_ERROR;
                }
                dbFile.createNewFile();
                fileCopy(backup, dbFile);
                return RESTORE_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
                return BACKUP_ERROR;
            }
        } else {
            return BACKUP_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case BACKUP_SUCCESS:
                if (listener != null) {
                    listener.onBackupComplete();
                }
                break;
            case RESTORE_SUCCESS:
                if (listener != null) {
                    listener.onRestoreComplete();
                }
                break;
            case RESTORE_NO_FILE_ERROR:
                if (listener != null) {
                    listener.onError(RESTORE_NO_FILE_ERROR);
                }
                break;
            default:
                if (listener != null) {
                    listener.onError(BACKUP_ERROR);
                }
        }
    }

    private void fileCopy(File source, File dest) throws IOException {
        FileChannel inChannel = new FileInputStream(source).getChannel();
        FileChannel outChannel = new FileOutputStream(dest).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}

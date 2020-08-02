package com.liuuuu.datapersistdemo.utils;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static final String TAG = "ZipUtils";

    public static void zipFile(File src, File dest) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(dest);
            zos = new ZipOutputStream(fos);
            zipFile(src, src.getName(), zos);
        } catch (FileNotFoundException e) {
            Log.e(TAG, String.format("File %s not found error", dest.getName()));
        } finally {
            if (zos != null) {
                try {
                    zos.flush();
                } catch (IOException e) {
                    Log.e(TAG, "IO error zos flush");
                }
                try {
                    zos.close();
                } catch (IOException e) {
                    Log.e(TAG, "IO error zos close last");
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "IO error fos close");
                }
            }
        }

    }

    private static void zipFile(File src, String relativeName, ZipOutputStream zos) {
        Log.i(TAG, "File: " + src.getAbsolutePath() + " relativeName: " + relativeName);
        if (src.isDirectory()) {
            relativeName = relativeName.concat(File.separator);
            File[] files = src.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isHidden();
                }
            });
            try {
                zos.putNextEntry(new ZipEntry(relativeName)); // 创建一个文件夹
            } catch (IOException e) {
                Log.e(TAG, "IO error putNextEntry dir " + relativeName);
                try {
                    zos.close();
                } catch (IOException ex) {
                    Log.e(TAG, "IO error zos close " + relativeName);
                }
            }
            for (File f : files) {
                zipFile(f, relativeName + f.getName(), zos);
            }
        } else {
            FileInputStream fis = null;
            try {
                zos.putNextEntry(new ZipEntry(relativeName));
            } catch (IOException e) {
                Log.e(TAG, "IO error putNextEntry file " + relativeName);
                try {
                    zos.close();
                } catch (IOException ex) {
                    Log.e(TAG, "IO error zos close " + relativeName);
                }
            }
            try {
                fis = new FileInputStream(src);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.flush();
            } catch (FileNotFoundException e) {
                Log.e(TAG, String.format("File %s not found error", relativeName));
            } catch (IOException e) {
                Log.e(TAG, String.format("File %s read error", relativeName));
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IO error fis close " + relativeName);
                    }
                }
            }
        }
    }
}
